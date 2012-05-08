import os
import sys
import traceback

sys.path.append(os.path.abspath('../src'))

from xdust import dust

def test(name, code, model=None):
    print '\n-----------VALUE-----------\n'
    print code.strip()
    print '\n-----------PARSED----------\n'
    print dust.compile(code, name)
    if model != None:
        print '\n-----------RENDERED--------\n'
        def callback(err, out):
            if err:
                import traceback
                traceback.print_exc(err)
            else:
                print out
        dust.render(name, model, callback)

#print "Keys"
test('basic', """
Hello {name}!
""", {
    'name': 'Fred'
    })
#print "Unescaped"
test('basic', """
Hello {name|s}
""", {
    'name': '<em>Fred</em>'
    })
#print "HTML escaping"
test('basic', """
Hello {name|h}
""", {
    'name': '<em>Fred</em>'
    })
#print "JavaScript escaping"
test('basic', """
Hello {name|j}
""", {
    'name': '"Fred"'
    })
#print "encodeURI escaping"
test('basic', """
Hello {name|u}
""", {
    'name': 'Fred Smith'
    })
#print  "encodeURIComponent escaping"
test('basic', """
Hello {name|uc}
""", {
    'name': 'Fred/Smith'
    })
#print "Multiple escapes"
test('basic', """
Hello {name|s|h|u}
""", {
    'name': '<em>Fred Smith</em>'
    })
#print "Section"
test('basic', """
{#friends}
  {name}, {age}{~n}
{/friends}
""", {
    'friends': [
        {'name': 'Moe', 'age': 37},
        {'name': 'Larry', 'age': 39},
        {'name': 'Curly', 'age': 35}
        ]
    })
#print "Alternate section bodys"
test('basic', """
{#friends}
  {name}, {age}{~n}
{:else}
  You have no friends!
{/friends}
""", {
    'friends': [
        {'name': 'Moe', 'age': 37},
        {'name': 'Larry', 'age': 39},
        {'name': 'Curly', 'age': 35}
        ]
    })
#print "Alternate section bodys (else)"
test('basic', """
{#friends}
  {name}, {age}{~n}
{:else}
  You have no friends!
{/friends}
""", {
    'friends': []
    })
#print "Self-closing tags"
test('basic', """
{#friends/}
""", {
    'friends': []
    })
#print "Paths relative to current context"
test('basic', """
{#names}{.} {/names}
""", {
    'names': ['Moe', 'Larry', 'Curly']
    })
#print "Path into a nested context"
test('basic', """
{foo.bar}
""", {
    'foo': {
        'bar': 'It worked'
        }
    })
#print "Constrained lookup to current scope"
test('basic', """
{.foo}
""", {
    'foo': 'it worked'
    })
#print "Inline parameters"
test('basic', """
{#profile bar="baz" bing="bong"}
  {name}, {bar}, {bing}
{/profile}
""", {
    'profile': {
        'name': 'Fred'
        }
    })
#print "Inline parameters to avoid namespace conflicts"
test('basic', """
{name}{~n}
{#profile root_name=name}
  {name}, {root_name}
{/profile}
""", {
    'name': 'Foo',
    'profile': {
        'name': 'Bar'
        }
    })
#print "Interpolated string parameters"
test('basic', """
{#snippet id="{name}_id"}
  {id}
{/snippet}
""", {
    'name': 'Fred',
    'snippet': [1, 2, 3]
    })
#print "Exists and notexists sections"
test('basic', """
{?tags}
  <ul class="{foo}">
    {#tags}
      <li>{.}</li>
    {/tags}
  </ul>
{:else}
  No tags!
{/tags}
""", {
    'foo': 'bar',
    'tags': ['a', 'b', 'c']
    })
#print "Context helpers, idx and sep"
test('basic', """
{#names}{.}{@idx}{.}{/idx}{@sep}, {/sep}{/names}
""", {
    'names': ['Moe', 'Larry', 'Curly']
    })
#print "Merged contexts"
test('basic', """
{#list:projects}{name}{@sep}, {/sep}{/list}
""", {
    'list': [1, 2, 3, 4, 5],
    'projects': [
        {'name': 'alpha'},
        {'name': 'beta'},
        {'name': 'omega'}
        ]
    })
#print "Special characters"
test('basic', """
Hello{~n}World!
""", {})
#print "Comments"
test('basic', """
{!
  Multiline
  {#foo}{bar}{/foo}
!}
{!before!}Hello{!after!}
""", {})
#print "Block partials"
test('example', """
Start{~n}
{+title}
  Base Title
{/title}
{~n}
{+main}
  Base Content
{/main}
{~n}
End
""", {})
#print "Partials/template includes"
test('profile', """
Start{~n}
{+title}
  {name}, {role}
{/title}
Middle{~n}
{+main}
  {#degrees}{.}{@sep},{/sep}{/degrees}
{/main}
End
""", {
    'name': 'John Doe',
    'role': 'CEO',
    'degrees': ['MBA', 'PMP', 'BS'],
    'user': {
        'name': 'Jane Doe',
        'role': 'CMO',
        'degress': ['BFA']
        }
    })
test('example', """
{>profile/}
""", {
    'name': 'John Doe',
    'role': 'CEO',
    'degrees': ['MBA', 'PMP', 'BS'],
    'user': {
        'name': 'Jane Doe',
        'role': 'CMO',
        'degress': ['BFA']
        }
    })
#print "Partial with context"
test('example', """
{>profile:user/}
""", {
    'name': 'John Doe',
    'role': 'CEO',
    'degrees': ['MBA', 'PMP', 'BS'],
    'user': {
        'name': 'Jane Doe',
        'role': 'CMO',
        'degrees': ['BFA']
        }
    })
#print "Inline partial tags overriding block contents of base"
test('example', """
{>profile/}
{<title}
  Hey there {name}!
{/title}
{<main}
  I know you have all these fancy degrees, so hopefully
  they make you a good {role}.
{/main}
""", {
    'name': 'John Doe',
    'role': 'CEO',
    'degrees': ['MBA', 'PMP', 'BS'],
    'user': {
        'name': 'Jane Doe',
        'role': 'CMO',
        'degrees': ['BFA']
        }
    })
#print "Self-closing block"
test('example', """
{+block/}
""", {})
#print "String literals in partial tags"
test('example', """
{>"~/templates/external.dust.html"/}
""", {
    'name': 'John Doe',
    'role': 'CEO',
    'degrees': ['MBA', 'PMP', 'BS'],
    'user': {
        'name': 'Jane Doe',
        'role': 'CMO',
        'degrees': ['BFA']
        }
    })

#print "Interpolated string literals in partial tags"
test('example', """
{#employees}
  {>"~/templates/{type}.dust.html"/}
{/employees}
""", {
    'employees': [
        {
            'type': 'foo',
            'name': 'John Doe',
            'role': 'CEO',
            'degrees': ['MBA', 'PMP', 'BS']
            },
        {
            'type': 'bar',
            'name': 'Jane Doe',
            'role': 'CMO',
            'degrees': ['BFA']
            },
        ]
    })
#print "Not in model"
test('example', """
Can you see me? {no}
""", {
    'yes': 'Yes'
    })
