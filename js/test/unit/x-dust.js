(function(dust) { 

describe('x-dust', function() {
  function render(name, code, model, expected) {
    return function() {
      var value = null,
          flag = false;
      runs(function() {
        if (code) {
          dust.compile(code, name);
        }
        dust.render(name, model, function(err, out) {
          if (err) {
            throw err;
          } else {
            value = out;
            flag = true;
          }
        });
      });
      waitsFor(function() {
        return flag;
      });
      runs(function() {
        expect(value).toBe(expected);
      });
    };
  }
  var model = {name: 'John Doe', role: 'CEO', degrees: ['MBA', 'PMP', 'BS'], user: {name: 'Jane Doe', role: 'CMO', degrees: ['BFA']}};
  beforeEach(function() {
    dust.compile('Start{~n}{+title}{name}, {role}{/title}Middle{~n}{+main}{#degrees}{.}{@sep},{/sep}{/degrees}{/main}End', 'profile');
    //model = {name: 'John Doe', role: 'CEO', degrees: ['MBA', 'PMP', 'BS'], user: {name: 'Jane Doe', role: 'CMO', degrees: ['BFA']}};
  });
  describe('Variables', function() {
    it('can be rendered', render('ex', 'Hello {name}!', {name: 'Fred'}, 'Hello Fred!'));
    it('render as blank when the value does not exist', render('ex', '{no}', {}, ''));
    it('are HTML-escaped by default', render('ex', 'Hello {name}!', {name: '<em>Fred</em>'}, 'Hello &lt;em&gt;Fred&lt;/em&gt;!'));
    it('can render as unescaped HTML', render('ex', 'Hello {name|s}!', {name: '<em>Fred</em>'}, 'Hello <em>Fred</em>!'));
    it('can render as escaped HTML', render('ex', 'Hello {name|h}!', {name: '<em>Fred</em>'}, 'Hello &lt;em&gt;Fred&lt;/em&gt;!'));
    it('can render as escaped JS', render('ex', 'Hello {name|j}!', {name: '"Fred"'}, 'Hello \\"Fred\\"!'));
    it('can render as escaped URI', render('ex', 'Hello {name|u}!', {name: 'Fred Smith'}, 'Hello Fred%20Smith!'));
    it('can render as escaped URI component', render('ex', 'Hello {name|uc}!', {name: 'Fred Smith'}, 'Hello Fred%20Smith!'));
    it('can be escaped multiple ways', render('ex', 'Hello {name|s|h|uc}!', {name: '<em>Fred Smith</em>'}, 'Hello %26lt%3Bem%26gt%3BFred%20Smith%26lt%3B%2Fem%26gt%3B!'));
  });
  describe('~ operator', function() {
    it('can render a newline', render('ex', '{~n}', '', '\n'));
    it('can render a carriage return', render('ex', '{~r}', '', '\r'));
    it('can render a space', render('ex', '{~s}', '', ' '));
    it('can render a left brace', render('ex', '{~lb}', '', '{'));
    it('can render a right brace', render('ex', '{~rb}', '', '}'));
  });
  describe('# operator', function() {
    it('can iterate through an array', render('ex', '{#friends}{name}, {age}{~n}{/friends}', {friends: [{name: 'Moe', age: 37}, {name: 'Larry', age: 39}, {name: 'Curly', age: 35}]}, 'Moe, 37\nLarry, 39\nCurly, 35\n'));
    it('can support a success block', render('ex', '{#friends}{name}, {age}{~n}{:else}You have no friends!{/friends}', {friends: [{name: 'Moe', age: 37}, {name: 'Larry', age: 39}, {name: 'Curly', age: 35}]}, 'Moe, 37\nLarry, 39\nCurly, 35\n'));
    it('can support a else block', render('ex', '{#friends}{name}, {age}{~n}{:else}You have no friends!{/friends}', {friends: []}, 'You have no friends!'));
    it('can self-close', render('ex', '{#friends/}', {friends: []}, ''));
    it('can test for truthy values', render('ex', '{#foo}{.}{:else}no{/foo}', {foo: 1}, '1'));
    it('can test for falsy values', render('ex', '{#foo}yes{:else}{.}{/foo}', {foo: 0}, '0'));
    it('can have static attributes', render('ex', '{#profile bar="baz" bing="bong"}{name}, {bar}, {bing}{/profile}', {profile: {name: 'Fred'}}, 'Fred, baz, bong'));
    it('can have variable attributes', render('ex', '{name}{~n}{#profile root_name=name}{name}, {root_name}{/profile}', {name: 'Foo', profile: {name: 'Fred'}}, 'Foo\nFred, Foo'));
    it('can have mixed attributes', render('ex', '{#snippet id="{name}_id"}{.} {id}{~n}{/snippet}', {name: 'Fred', snippet: [1, 2, 3]}, '1 Fred_id\n2 Fred_id\n3 Fred_id\n'));
    it('can force the context to a different variable', render('ex', '{#list:projects}{name},{/list}', {list: [1, 2, 3], projects: [{name: 'alpha'}, {name: 'beta'}]}, 'alpha,beta,'));
  });
  describe('. operator', function() {
    it('refers to the current context object', render('ex', '{.}', 'foo', 'foo'));
    it('allows property access from the context object', render('ex', '{.foo}', {foo: 1}, '1'));
    it('allows nested property access', render('ex', '{foo.bar}', {foo: {bar: 1}}, '1'));
    it('refers to each item in an array', render('ex', '{#names}{.} {/names}', {names: ['Moe', 'Larry', 'Curly']}, 'Moe Larry Curly '));
  });
  describe('? operator', function() {
    it('can test logic without consuming scope', render('ex', '{?tags}<ul class="{foo}">{#tags}<li>{.}</li>{/tags}</ul>{:else}No tags!{/tags}', {foo: 'bar', tags: ['a', 'b', 'c']}, '<ul class="bar"><li>a</li><li>b</li><li>c</li></ul>'));
    it('can support an else block', render('ex', '{?tags}<ul class="{foo}">{#tags}<li>{.}</li>{/tags}</ul>{:else}No tags!{/tags}', {foo: 'bar', tags: []}, 'No tags!'));
  });
  describe('^ operator', function() {
    it('can output inner content when tested against an undefined value', render('ex', '{^tags}No tags!{:else}Yes tags!{/tags}', {foo: 'bar'}, 'No tags!'));
    it('can output inner content when tested against a defined but falsey value', render('ex', '{^tags}No tags!{/tags}', {foo: 'bar', tags: 0}, 'No tags!'));
    it('can output inner content when tested against an empty array', render('ex', '{^tags}No tags!{/tags}', {foo: 'bar', tags: []}, 'No tags!'));
    it('can ignore inner content when tested against a truthy value', render('ex', '{^tags}no {/tags}tags here', {foo: 'bar', tags: ['a', 'b', 'c']}, 'tags here'));
  	it('can support an else block', render('ex', '{^tags}No tags!{:else}<ul class="{foo}">{#tags}<li>{.}</li>{/tags}</ul>{/tags}', {foo: 'bar', tags: ['a', 'b', 'c']}, '<ul class="bar"><li>a</li><li>b</li><li>c</li></ul>'));
  });
  describe('! operator', function() {
    it('can comment out multiple lines of text', render('ex', '{!\nMultiline {#foo}{bar}{/foo}!}{!before!}Hello{!after!}', {}, 'Hello'));
  });
  describe('+ operator', function() {
    it('can define sections', render('ex','Start{~n}{+title}Base Title{/title}{~n}{+main}Base Content{/main}{~n}End', {}, 'Start\nBase Title\nBase Content\nEnd'));
    it('can include variables in sections', render('profile', null, model, 'Start\nJohn Doe, CEOMiddle\nMBA,PMP,BSEnd'));
    it('can self-close', render('ex', '{+block/}', {}, ''));
  });
  describe('> operator', function() {
    it('can reference a template by name', render('ex', '{>profile/}', model, 'Start\nJohn Doe, CEOMiddle\nMBA,PMP,BSEnd'));
    it('can reference a template by URL', render('ex', '{>"~/templates/external.dust.html"/}', model, 'Start External\nJohn Doe, CEO\nMiddle External\nMBA,PMP,BS\nEnd External'));
    it('can reference a template with scope', render('ex', '{>profile:user/}', model, 'Start\nJane Doe, CMOMiddle\nBFAEnd'));
    it('can reference a template within a loop', render('ex', '{#employees}{>"~/templates/{type}.dust.html"/}{/employees}', {employees: [{type: 'foo', name: 'John Doe', role: 'CEO', degrees: ['MBA', 'PMP', 'BS']}, {type: 'bar', name: 'Jane Doe', role: 'CMO', degrees: ['BFA']}]}, 'THE FOO TEMPLATE\nJohn Doe, CEO\nMiddle External\nMBA,PMP,BS\nEnd ExternalTHE BAR TEMPLATE\nJane Doe, CMO\nMiddle External\nBFA\nEnd External'));
  });
  describe('< operator', function() {
    it('can override sections in a referenced template', render('ex', '{>profile/}{<title}Hi {name}!{/title}{<main}You are a good {role}.{/main}', model, 'Start\nHi John Doe!Middle\nYou are a good CEO.End'));
  });
  describe('@idx', function() {
    it('can render the index during iteration', render('ex', '{#names}{.}{@idx}{.}{/idx}{/names}', {names: ['Moe', 'Larry', 'Curly']}, 'Moe0Larry1Curly2'));
  });
  describe('@sep', function() {
    it('can render a separator during iteration', render('ex', '{#names}{.}{@sep},{/sep}{/names}', {names: ['Moe', 'Larry', 'Curly']}, 'Moe,Larry,Curly'));
  });
});

})(xdust);
