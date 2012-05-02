using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using XDust;

namespace XDustTest
{
    class Program
    {
        static XDust.XDust dust = new XDust.XDust();

        static void Print(String value)
        {
            Console.Out.WriteLine(value);
        }

        static void Test(String name, String code, Object model)
        {
            Print("-----------VALUE-----------");
            Print(code.Trim());
            Print("-----------PARSED-----------");
            Print(dust.Compile(code, name, null).ToString());
            if (null != model)
            {
                Print("-----------RENDERED-----------");
                dust.Render(name, model, (e, s) => Print(e == null ? s : e.ToString()));
            }
        }

        static void Main(string[] args)
        {
            Test("basic", @"Hello {name}!", new { name = "Fred" });
            Test("basic", @"Hello {name|s}!", new { name = "<em>Fred</em>" });
            Test("basic", @"Hello {name|h}!", new { name = "<em>Fred</em>" });
            Test("basic", @"Hello {name|j}!", new { name = "\"Fred\"" });
            Test("basic", @"Hello {name|uc}!", new { name = "Fred/Smith" });
            Test("basic", @"Hello {name|s|h|u}!", new { name = "<em>Fred Smith</em>" });
            Test("basic", @"
{#friends}
  {name}, {age}{~n}
{/friends}", new
           {
               friends = new[] {
               new { name = "Moe", age = 37 },
               new { name = "Larry", age = 39 },
               new { name = "Curly", age = 35 }
           }
           });
            Test("basic", @"
{#friends}
  {name}, {age}{~n}
{:else}
  You have no friends!
{/friends}", new
           {
               friends = new[] {
               new { name = "Moe", age = 37 },
               new { name = "Larry", age = 39 },
               new { name = "Curly", age = 35 }
           }
           });
            Test("basic", @"
{#friends}
  {name}, {age}{~n}
{:else}
  You have no friends!
{/friends}", new
           {
               friends = new Object[] { }
           });
            Test("basic", @"
{#friends/}", new
           {
               friends = new Object[] { }
           });
            Test("basic", @"
{#names}{.} {/names}", new
           {
               names = new String[] { "Moe", "Larry", "Curly" }
           });
            Test("basic", @"
{foo.bar}", new
                     {
                         foo = new { bar = "It worked" }
                     });
            Test("basic", @"
{.foo}", new
          {
              foo = "It worked"
          });
            Test("basic", @"
{#profile bar=""baz"" bing=""bong""}
  {name}, {bar}, {bing}
{/profile}", new
       {
           profile = new { name = "Fred" }
       });
            Test("basic", @"
{name}{~n}
{#profile root_name=name}
  {name}, {root_name}
{/profile}", new
           {
               name = "Foo",
               profile = new { name = "Bar" }
           });
            Test("basic", @"
{#snippet id=""{name}_id""}
  {id}
{/snippet}", new
           {
               name = "Fred",
               snippet = new int[] { 1, 2, 3 }
           });
            Test("basic", @"
{?tags}
  <ul class=""{foo}"">
    {#tags}
      <li>{.}</li>
    {/tags}
  </ul>
{:else}
  No tags!
{/tags}", new
           {
               foo = "bar",
               tags = new String[] { "a", "b", "c" }
           });
            Test("basic", @"
{#names}{.}{@idx}{.}{/idx}{@sep}, {/sep}{/names}", new
        {
            names = new String[] { "Moe", "Larry", "Curly" }
        });
            Test("basic", @"
{#names}{.}{@idx}{.}{/idx}{@sep}, {/sep}{/names}", new
         {
             names = new String[] { "Moe", "Larry", "Curly" }
         });
            Test("basic", @"
{#list:projects}{name}{@sep}, {/sep}{/list}", new
        {
            list = new int[] { 1, 2, 3, 4, 5 },
            projects = new[] {
                new { name = "alpha" },
                new { name = "beta" },
                new { name = "omega" }
            }
        });
            Test("basic", @"Hello{~n}World!", new Object { });
            Test("basic", @"
{!
  Multiline
  {#foo}{bar}{/foo}
!}
{!before!}Hello{!after!}", new Object { });
            Test("basic", @"
Start{~n}
{+title}
  Base Title
{/title}
{~n}
{+main}
  Base Content
{/main}
{~n}
End", new Object { });
            Test("profile", @"
Start{~n}
{+title}
  {name}, {role}
{/title}
Middle{~n}
{+main}
  {#degrees}{.}{@sep},{/sep}{/degrees}
{/main}
End", new
    {
        name = "John Doe",
        role = "CEO",
        degrees = new String[] { "MBA", "PMP", "BS" },
        user = new
        {
            name = "Jane Doe",
            role = "CMO",
            degrees = new String[] { "BFA" }
        }
    });
            Test("example", @"
{>profile/}", new
    {
        name = "John Doe",
        role = "CEO",
        degrees = new String[] { "MBA", "PMP", "BS" },
        user = new
        {
            name = "Jane Doe",
            role = "CMO",
            degrees = new String[] { "BFA" }
        }
    });
            Test("example", @"
{>profile:user/}", new
            {
                name = "John Doe",
                role = "CEO",
                degrees = new String[] { "MBA", "PMP", "BS" },
                user = new
                {
                    name = "Jane Doe",
                    role = "CMO",
                    degrees = new String[] { "BFA" }
                }
            });
            Test("example", @"
{>profile/}
{<title}
  Hey there {name}!
{/title}
{<main}
  I know you have all these fancy degrees, so hopefully
  they make you a good {role}.
{/main}", new
                 {
                     name = "John Doe",
                     role = "CEO",
                     degrees = new String[] { "MBA", "PMP", "BS" },
                     user = new
                     {
                         name = "Jane Doe",
                         role = "CMO",
                         degrees = new String[] { "BFA" }
                     }
                 });
            Test("example", @"{+block/}", new Object { });
            Test("example", @"
{>""templates/external.dust.html""/}
", new
        {
            name = "John Doe",
            role = "CEO",
            degrees = new String[] { "MBA", "PMP", "BS" },
            user = new
            {
                name = "Jane Doe",
                role = "CMO",
                degrees = new String[] { "BFA" }
            }
        });
            Test("example", @"
{#employees}
  {>""templates/{type}.dust.html""/}
{/employees}
", new
 {
     employees = new[] {
         new {
             type = "foo",
             name = "John Doe",
             role = "CEO",
             degrees = new String[] { "MBA", "PMP", "BS" }
         },
         new {
             type = "bar",
             name = "Jane Doe",
             role = "CMO",
             degrees = new String[] { "BFA" }
         }
     }
 });
            Test("example", @"
Can you see me? {no}
", new { yes = "Yes" });
            Console.ReadKey();
        }
    }
}
