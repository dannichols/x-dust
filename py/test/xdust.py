"""
dust.py 0.5.0

Copyright (c) 2012 Dan Nichols

Released under the MIT license.

dust.js 0.3.0

Copyright (c) 2010 Aleksander Williams

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
"""

import re
import urllib
from cStringIO import StringIO

class Context(object):
    def __init__(self, head, tail, params):
        self.head = head
        self.tail = {}
        self.tail_is_value = not isinstance(tail, dict) and not isinstance(tail, Context)
        self.params = {}
        if params:
            self.params.update(params)
        self.update(tail)
    def get(self, name):
        if not self.tail_is_value and name in self.tail:
            return self.tail[name]
        elif name in self.params:
            return self.params[name]
        elif self.head:
            return self.head.get(name)
        else:
            return None
    def __getitem__(self, name):
        if self.tail_is_value:
            raise Exception('Cannot get property on value type')
        else:
            return self.tail[name] if name in self.tail else None
    def __setitem__(self, name, value):
        if self.tail_is_value:
            raise Exception('Cannot set property on value type')
        else:
            self.tail[name] = value
    def __iter__(self):
        return iter(self.tail) if not self.tail_is_value else None
    def update(self, other):
        if other != None:
            if isinstance(other, Context):
                self.tail_is_value = other.tail_is_value
                if other.tail_is_value:
                    self.tail = other.tail
                else:
                    if self.tail_is_value:
                        self.tail = {}
                        self.tail_is_value = False
                    self.tail.update(other.tail)
                self.params.update(other.params)
            elif isinstance(other, dict):
                if self.tail_is_value:
                    self.tail = {}
                    self.tail_is_value = False
                for k, v in other.items():
                    self[k] = v
            else:
                self.tail_is_value = True
                self.tail = other

class ContextResolver(object):
    def __init__(self, path):
        self.path = []
        if path.startswith('.'):
            self.path += ['.']
            self.path += path[1:].split('.')
        else:
            self.path = path.split('.')
    def __str__(self):
        path = []
        path.extend(self.path)
        if path[0] == '.':
            path[0] = ''
        return str('.'.join(path))
    def resolve(self, context, model):
        copy = Context(context, model, None)
        if model:
            if isinstance(model, list):
                copy = []
                copy.extend(model)
            else:
                for segment in self.path:
                    if segment:
                        if segment == '.':
                            if copy.tail_is_value:
                                copy = copy.tail
                                break
                            else:
                                copy.parent = None
                        else:
                            value = copy.get(segment)
                            if value != None:
                                copy = Context(copy, value, None)
                                if copy.tail_is_value:
                                    copy = copy.tail
                            else:
                                copy = None
                                break
                    else:
                        copy = copy.tail
                        break
        else:
            copy = None
        return copy

class RenderChain(object):
    def __init__(self, head, tail):
        self.tail = tail
        self.head = head
    def get_block(self, name):
        block = self.tail.get_block(name)
        if block:
            return block
        elif self.head:
            return self.head.get_block(name)
        else:
            return None
    def __str__(self):
        return '[[%s] [%s]]' % (self.tail, str(self.head))

class Node(object):
    def __init__(self):
        self.blocks = {}
    def render(self, chain, context, model):
        raise Exception('Abstract')
    def __str__(self):
        return '<Node>'
    def get_block(self, name):
        if self.has_block(name):
            return self.blocks[name].as_block()
        else:
            return None
    def set_block(self, name, value):
        self.blocks[name] = value
    def has_block(self, name):
        return name in self.blocks
    def as_block(self):
        return self

class NodeList(Node):
    def __init__(self, nodes=None):
        super(NodeList, self).__init__()
        self.nodes = []
        if nodes:
            self.nodes.extend(nodes)
    def __iter__(self):
        return iter(self.nodes)
    def add(self, node):
        self.nodes.append(node)
        return node
    def last(self):
        count = len(self.nodes)
        if count > 0:
            return self.nodes[count - 1]
        else:
            return None
    def prepare_model(self, chain, context, model):
        return Context(context, model, None)
    def render(self, chain, context, model):
        chain = RenderChain(chain, self)
        model = self.prepare_model(chain, context, model)
        sb = StringIO()
        if model != None:
            for node in self.nodes:
                sb.write(node.render(chain, context, model))
        out = sb.getvalue()
        sb.close()
        return out
    def __str__(self):
        return ''.join([str(n) for n in self.nodes])

class TextNode(Node):
    def __init__(self, value=''):
        super(TextNode, self).__init__()
        self.buffer = StringIO()
        self.buffer.write(value)
        self.value = value
    def write(self, value):
        self.buffer.write(value)
    def close(self):
        self.value = self.buffer.getvalue()
        self.buffer.close()
        self.buffer = None
    def render(self, chain, context, model):
        if self.buffer:
            self.close()
        return self.value
    def __str__(self):
        if self.buffer:
            self.close()
        return self.value

class CommentNode(TextNode):
    def render(self, chain, context, model):
        return ''
    def __str__(self):
        return '<CommentNode>'

class VariableNode(Node):
    def __init__(self, path, filters=None):
        super(VariableNode, self).__init__()
        self.context = ContextResolver(path)
        self.filters = []
        if filters:
            self.filters.extend(filters)
    def __str__(self):
        return '{%s%s}' % (str(self.context), '|' + '|'.join(self.filters) if self.filters else '')
    def render(self, chain, context, model):
        chain = RenderChain(chain, self)
        orig_model = model
        model = self.context.resolve(context, model)
        result = ''
        if model != None:
            if isinstance(model, Node):
                result = model.render(chain, context, orig_model)
            elif isinstance(model, ContextResolver):
                result = model.resolve(context, orig_model)
            elif isinstance(model, Context):
                result = model.value
            else:
                result = str(model)
            if self.filters:
                for flag in self.filters:
                    if flag in dust.filters:
                        result = dust.filters[flag](result)
            else:
                result = dust.escape_html(result)
        return result

class LogicNode(Node):
    operator = '#'
    allow_iter = True
    def __init__(self, path, scope, params):
        super(LogicNode, self).__init__()
        self.bodies = {}
        self.start_body('block')
        self.params = {}
        if params:
            self.params.update(params)
        self.context = ContextResolver(path) if path else None
        self.scope = ContextResolver(scope) if scope else None
    def __str__(self):
        result = '{%s%s' % (self.operator, str(self.context))
        if self.scope:
            result += ':%s' % str(self.scope)
        for name in self.params:
            result += ' %s=' % name
            value = self.params[name]
            if isinstance(value, NodeList):
                result += '"'
                for node in value:
                    result += str(node)
                result += '"'
            elif isinstance(value, VariableNode):
                result += str(value.context)
            elif isinstance(value, TextNode):
                result += '"%s"' % str(value)
            else:
                result += str(value)
        result += '}%s' % str(self.bodies['block'])
        for name in self.bodies:
            if name != 'block':
                result += '{:%s}%s' % (name, str(self.bodies[name]))
        result += '{/%s}' % str(self.context)
        return result
    def start_body(self, name):
        body = NodeList()
        self.current_body = self.bodies[name] = body
        return body
    def end_body(self):
        self.current_body = None
    def prepare_model(self, chain, context, model):
        params = {}
        if self.params:
            for name in self.params:
                params[name] = self.params[name].render(chain, context, context)
        result = Context(context, model, params)
        result.update(model)
        return result
    def render_body(self, name, chain, context, model):
        sb = StringIO()
        body = self.bodies[name] if name in self.bodies else None
        context = Context(context, model, self.params)
        if self.scope:
            model = self.scope.resolve(context, model)
        elif self.context:
            model = self.context.resolve(context, model)
        if body:
            chain = RenderChain(chain, body)
            if self.allow_iter and isinstance(model, list):
                length = len(model)
                if name == 'else' and length < 1:
                    sb.write(body.render(chain, context, None))
                else:
                    for i in range(length):
                        iter_model = self.prepare_model(chain, context, model[i])
                        iter_model.params.update({
                            '@idx': i,
                            '@sep': i != length - 1
                            })
                        sb.write(body.render(chain, context, iter_model))
            else:
                iter_model = self.prepare_model(chain, context, model)
                sb.write(body.render(chain, context, iter_model))
        out = sb.getvalue()
        sb.close()
        return out
    def choose_body_name(self, context, model):
        resolved = self.context.resolve(context, model)
        if isinstance(resolved, list) and len(resolved) < 1:
            resolved = False
        return 'block' if resolved else 'else'
    def render(self, chain, context, model):
        chain = RenderChain(chain, self)
        context = Context(context, None, self.params)
        body_name = self.choose_body_name(context, model)
        return self.render_body(body_name, chain, context, model)

class ExistsNode(LogicNode):
    operator = '?'
    allow_iter = False
    def prepare_model(self, chain, context, model):
        return Context(context, context, self.params)

class NotExistsNode(ExistsNode):
    def choose_body_name(self, context, model):
        return 'else' if self.context.resolve(context, model) else 'block'

class HelperNode(LogicNode):
    allow_iter = False
    def __init__(self, name, scope, params):
        super(HelperNode, self).__init__(None, scope, params)
        self.context = name
    def render(self, chain, context, model):
        chain = RenderChain(chain, self)
        context = Context(props=self.params, parent=context)
        helper = dust.helpers[self.context]
        return helper(chain, context, model)

class IndexNode(NodeList):
    def prepare_model(self, chain, context, model):
        if model.get('@idx') != None:
            return str(model.get('@idx'))
        else:
            return None
    def __str__(self):
        return '{@idx}%s{/idx}' % super(IndexNode, self).__str__()

class SepNode(NodeList):
    def prepare_model(self, chain, context, model):
        if model.get('@sep'):
            return super(SepNode, self).prepare_model(chain, context, model)
        else:
            return None
    def __str__(self):
        return '{@sep}%s{/sep}' % super(SepNode, self).__str__()

class EscapedCharacterNode(Node):
    characters = {
        'n': '\n',
        'r': '\r',
        's': ' ',
        'lb': '{',
        'rb': '}'
        }
    def __init__(self, code):
        super(EscapedCharacterNode, self).__init__()
        self.code = code
        self.character = self.characters[code]
    def __str__(self):
        return '{~%s}' % self.code
    def render(self, chain, context, model):
        return self.character

class PartialNode(Node):
    def __init__(self, include, scope):
        super(PartialNode, self).__init__()
        self.include = include
        self.scope = ContextResolver(scope) if scope else None
    def __str__(self):
        result = '{>%s' % self.include
        if self.scope:
            result += ':%s' % str(self.scope)
        return '%s/}' % result
    def render(self, chain, context, model):
        if self.scope:
            model = self.scope.resolve(context, model)
        chain = RenderChain(chain, self)
        name = self.include
        if isinstance(self.include, NodeList):
            name = self.include.render(chain, context, model)
        template = dust.load(name)
        return template.render(chain, context, model)

class BlockNode(NodeList):
    def __init__(self, name):
        super(BlockNode, self).__init__()
        self.name = name
    def __str__(self):
        return '{+%s}%s{/%s}' % (self.name, super(BlockNode, self).__str__(), self.name)
    def render(self, chain, context, model):
        block = chain.get_block(self.name)
        if block:
            return block.render(chain, context, model)
        else:
            return super(BlockNode, self).render(chain, context, model)

class InlinePartialNode(NodeList):
    def __init__(self, name):
        super(InlinePartialNode, self).__init__()
        self.name = name
    def __str__(self):
        return '{<%s}%s{/%s}' % (self.name, super(InlinePartialNode, self).__str__(), self.name)
    def as_block(self):
        result = NodeList()
        result.nodes.extend(self.nodes)
        return result
    def render(self, chain, context, model):
        return ''

class NodeListParser(object):
    def __init__(self):
        self.last_end = 0
    def parse(self, string):
        string = re.sub(r'\{!.+?!\}', '', string, flags=re.DOTALL).strip()
        nodes = [NodeList()]
        depth = 0
        exp = re.compile(r'(\{[\~\#\?\@\:\<\>\+\/\^]?([a-zA-Z0-9_\$\.]+|"[^"]+")(\:[a-zA-Z0-9\$\.]+)?(\|[a-z]+)*?( \w+\=(("[^"]*?")|([\w\.]+)))*?\/?\})', flags=re.MULTILINE)
        last_end = 0
        start = None
        end = None
        for match in re.finditer(exp, string):
            depth_change = False
            start = match.start(1)
            end = match.end(1)
            if last_end != start:
                head = string[last_end:start]
                if head:
                    nodes[depth].add(TextNode(value=head))
            last_end = end
            node = None
            tag = string[start + 1:end - 1]
            operator = tag[0]
            tag = tag.split(' ')
            if operator in ['~', '#', '?', '@', ':', '<', '>', '+', '/', '^']:
                tag_name = tag[0][1:].split(':')
                scope = tag_name[1] if len(tag_name) > 1 else None
                tag_name = tag_name[0]
                params = None
                self_closed = tag_name.endswith('/')
                if self_closed:
                    tag_name = tag_name[:-1]
                elif scope and scope.endswith('/'):
                    scope = scope[:-1]
                    self_closed = True
                if operator == '~':
                    node = EscapedCharacterNode(tag_name)
                elif operator == '#':
                    if tag_name in dust.helpers:
                        node = HelperNode(tag_name, scope, params)
                    else:
                        node = LogicNode(tag_name, scope, params)
                elif operator == '?':
                    node = ExistsNode(tag_name, scope, params)
                elif operator == '@':
                    name = tag[0][1:]
                    if name == 'idx':
                        node = IndexNode()
                    elif name == 'sep':
                        node = SepNode()
                elif operator == '>':
                    is_external = tag_name.startswith('"')
                    if is_external:
                        tag_name = self.parse(tag_name.strip('"'))
                    node = PartialNode(tag_name, scope)
                elif operator == '+':
                    node = BlockNode(tag_name)
                elif operator == '<':
                    node = InlinePartialNode(tag_name)
                else:
                    node = TextNode(value='UNDEFINED:' + ' '.join(tag))
                if not self_closed:
                    if operator in ['#', '?', '@', ':', '+', '<']:
                        depth_change = True
                        for param in tag[1:]:
                            param = param.split('=')
                            name = param[0]
                            value = '='.join(param[1:])
                            if not value.startswith('"'):
                                value = VariableNode(value)
                            else:
                                value = self.parse(value.strip('"'))
                            node.params[name] = value
                        if isinstance(node, NodeList):
                            if isinstance(node, InlinePartialNode):
                                nodes[depth].set_block(node.name, node)
                            nodes[depth].add(node)
                            depth += 1
                            nodes.insert(depth, node)
                        elif isinstance(node, LogicNode):
                            nodes[depth].add(node)
                            depth += 1
                            nodes.insert(depth, node.current_body)
                        else:
                            root = nodes[depth - 1].last()
                            root.end_body()
                            nodes[depth] = root.start_body(tag_name)
                    elif operator in '/':
                        depth_change = True
                        if depth > 0:
                            nodes.pop(depth)
                            depth -= 1
            else:
                tag = tag[0].split('|')
                filters = tag[1:]
                tag = tag[0]
                node = VariableNode(tag, filters=filters)
            if node and not depth_change:
                nodes[depth].add(node)
        tail = string[last_end:]
        if tail:
            nodes[depth].add(TextNode(value=tail))
        return nodes[0]

class Template(object):
    def __init__(self, name, root_node, src_file=None):
        self.name = name
        self.root_node = root_node
        self.src_file = src_file
    def render(self, model, chain=None, context=None):
        return self.root_node.render(chain, context, model)

class Dust(object):
    def __init__(self):
        self.parser = NodeListParser()
        self.templates = {}
        self.helpers = {}
        self.filters = {
            'h': self.escape_html,
            'j': self.escape_js,
            'u': self.escape_uri,
            'uc': self.escape_uri_component
            }
    def escape_html(self, string):
        string = str(string)
        if not re.match(r'[&<>\"]', string):
            return string
        else:
            return string.replace('&', '&amp;').replace('<', '&lt;').replace('>', '&gt;').replace('"', '&quot;').replace("'", '&squot;')
    def escape_js(self, string):
        string = str(string)
        return string.replace('\\', '\\\\').replace('"', '\\"').replace("'", "\\'").replace('\r', '\\r').replace('\u2028', '\\u2028').replace('\u2029', '\\u2029').replace('\n', '\\n').replace('\f', '\\f').replace('\t', '\\t')
    def escape_uri(self, string):
        return urllib.quote(string)
    def escape_uri_component(self, string):
        return self.escape_uri(string).replace('/', '%2F').replace('?', '%3F').replace('=', '%3D').replace('&', '%26')
    def compile(self, string, name, src_file=None):
        root_node = self.parser.parse(string)
        self.templates[name] = Template(name, root_node, src_file=src_file)
        return root_node
    def load(self, src_file, name=None):
        if name == None:
            name = src_file
        if src_file in self.templates:
            return self.templates[name].root_node
        else:
            f = open(src_file, 'r')
            code = f.read()
            f.close()
            return self.compile(code, name, src_file=src_file)
    def render(self, name, model, callback):
        template = self.templates[name]
        out = None
        err = None
        try:
            out = template.render(model)
        except Exception as exc:
            err = exc
        callback(err, out)

dust = Dust()
