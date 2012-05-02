using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace XDust
{
    public class XDustNodeListParser
    {
        private static readonly Char[] Operators = new Char[] { '~', '#', '?', '@', ':', '<', '>', '+', '/', '^' };
        private static readonly Char[] SectionOperators = new Char[] { '#', '?', '@', ':', '+', '<' };

        public XDustNodeListParser()
        {
            this.LastEnd = 0;
        }

        private int LastEnd { get; set; }

        public XDustNodeList Parse(XDust dust, String str)
        {
            Regex comments = new Regex(@"\{!.+?!\}", RegexOptions.Singleline);
            str = comments.Replace(str, String.Empty).Trim();
            var nodes = new List<XDustNodeList>();
            nodes.Add(new XDustNodeList(null));
            int depth = 0;
            Regex exp = new Regex(@"(\{[\~\#\?\@\:\<\>\+\/\^]?([a-zA-Z0-9_\$\.]+|""[^""]+"")(\:[a-zA-Z0-9\$\.]+)?(\|[a-z]+)*?( \w+\=((""[^""]*?"")|([\w\.]+)))*?\/?\})", RegexOptions.Singleline);
            int lastEnd = 0;
            int? start = null;
            int? end = null;
            Match match = exp.Match(str);
            while (match.Success)
            {
                bool depthChange = false;
                start = match.Index;
                end = start + match.Length;
                if (lastEnd != start)
                {
                    String head;
                    int headDiff = start.Value - lastEnd;
                    if (headDiff <= 0)
                    {
                        head = str.Substring(lastEnd);
                    }
                    else
                    {
                        head = str.Substring(lastEnd, headDiff);
                    }
                    if (!String.IsNullOrEmpty(head))
                    {
                        nodes[depth].Add(new XDustTextNode(head));
                    }
                }
                lastEnd = end.Value;
                XDustNode node = null;
                String tag = str.Substring(start.Value + 1, end.Value - start.Value - 2);
                Char op = tag[0];
                String[] tagParts = tag.Split(' ');
                if (Operators.Contains(op))
                {
                    String[] tagNameParts = tagParts[0].Substring(1).Split(':');
                    bool selfClosed = tagNameParts[tagNameParts.Length - 1].EndsWith("/");
                    if (selfClosed)
                    {
                        int index = tagNameParts.Length - 1;
                        String last = tagNameParts[index];
                        tagNameParts[index] = last.Substring(0, last.Length - 1);
                    }
                    String scope = tagNameParts.Length > 1 ? tagNameParts[1] : null;
                    String tagName = tagNameParts[0];
                    Dictionary<String, XDustNode> parameters = null;
                    if (op == '~')
                    {
                        node = new XDustEscapedCharacterNode(tagName);
                        selfClosed = true;
                    }
                    else if (op == '#')
                    {
                        if (dust.Helpers.ContainsKey(tagName))
                        {
                            node = new XDustHelperNode(tagName, scope, parameters);
                        }
                        else
                        {
                            node = new XDustLogicNode(tagName, scope, parameters);
                        }
                    }
                    else if (op == '?')
                    {
                        node = new XDustExistsNode(tagName, scope, parameters);
                    }
                    else if (op == '@')
                    {
                        String name = tagParts[0].Substring(1);
                        if (name == "idx")
                        {
                            node = new XDustIndexNode(null);
                        }
                        else if (name == "sep")
                        {
                            node = new XDustSepNode(null);
                        }
                        else
                        {
                            throw new InvalidOperationException();
                        }
                    }
                    else if (op == '>')
                    {
                        bool isExternal = tagName.StartsWith("\"");
                        Object tagArgs = tagName;
                        if (isExternal)
                        {
                            tagArgs = this.Parse(dust, tagName.Trim('"'));
                        }
                        node = new XDustPartialNode(tagArgs, scope);
                    }
                    else if (op == '+')
                    {
                        node = new XDustBlockNode(tagName);
                    }
                    else if (op == '<')
                    {
                        node = new XDustInlinePartialNode(tagName);
                    }
                    else if (op == '/' || op == ':')
                    {
                        node = null;
                    }
                    else
                    {
                        throw new ArgumentOutOfRangeException();
                    }
                    if (!selfClosed)
                    {
                        if (SectionOperators.Contains(op))
                        {
                            depthChange = true;
                            foreach (String param in tagParts.Skip(1))
                            {
                                String[] paramParts = param.Split('=');
                                String name = paramParts[0];
                                String valueStr = String.Join("=", paramParts.Skip(1).ToArray());
                                XDustNode value;
                                if (!valueStr.StartsWith("\""))
                                {
                                    value = new XDustVariableNode(valueStr, null);
                                }
                                else
                                {
                                    value = this.Parse(dust, valueStr.Trim('"'));
                                }
                                ((IXDustSectionNode)node).Parameters[name] = value;
                            }
                            if (node is XDustNodeList)
                            {
                                if (node is XDustInlinePartialNode)
                                {
                                    nodes[depth].SetBlock(((XDustInlinePartialNode)node).Name, node);
                                }
                                nodes[depth].Add(node);
                                depth += 1;
                                nodes.Insert(depth, (XDustNodeList)node);
                            }
                            else if (node is XDustLogicNode)
                            {
                                nodes[depth].Add(node);
                                depth += 1;
                                nodes.Insert(depth, ((XDustLogicNode)node).CurrentBody);
                            }
                            else
                            {
                                var root = (IXDustSectionNode)nodes[depth - 1].Last();
                                root.EndBody();
                                nodes[depth] = root.StartBody(tagName);
                            }
                        }
                        else if (op == '/')
                        {
                            depthChange = true;
                            if (depth > 0)
                            {
                                nodes.RemoveAt(depth);
                                depth -= 1;
                            }
                        }
                    }
                    if (!selfClosed && null != node && !depthChange)
                    {
                        nodes[depth].Add(node);
                    }
                }
                else
                {
                    tagParts = tagParts[0].Split('|');
                    IEnumerable<String> filters = tagParts.Skip(1);
                    tag = tagParts[0];
                    node = new XDustVariableNode(tag, filters);
                }
                if (null != node && !depthChange)
                {
                    nodes[depth].Add(node);
                }
                match = match.NextMatch();
            }
            var tail = str.Substring(lastEnd);
            if (!String.IsNullOrEmpty(tail))
            {
                nodes[depth].Add(new XDustTextNode(tail));
            }
            return nodes[0];
        }
    }
}
