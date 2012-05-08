package com.heydanno.xdust.test;

import java.util.HashMap;
import java.util.Map;

import com.heydanno.xdust.IXDustResult;
import com.heydanno.xdust.XDust;

public class Program {

	static class Result implements IXDustResult {

		public Result() {

		}

		@Override
		public void call(Exception error, String output) {
			if (null != error) {
				if (null != error.getMessage()) {
					print(error.getMessage());
				}
				for (StackTraceElement el : error.getStackTrace()) {
					print(el.toString());
				}
			} else {
				print(output);
			}
		}

	}

	static XDust dust = new XDust();

	static void print(Object value) {
		System.out.println(value);
	}

	static void test(String name, String code, Object model) {
		print("-----------VALUE-----------");
		print(code.trim());
		print("-----------PARSED-----------");
		print(dust.compile(code, name, null).toString());
		if (null != model) {
			print("-----------RENDERED-----------");
			dust.render(name, model, new Result());
		}
	}

	public static void main(String[] args) {
		test("basic", "Hello {name}!", new ModelPerson("Fred"));
		test("basic", "Hello {name|s}!", new ModelPerson("<em>Fred</em>"));
		test("basic", "Hello {name|h}!", new ModelPerson("<em>Fred</em>"));
		test("basic", "Hello {name|j}!", new ModelPerson("\"Fred\""));
		test("basic", "Hello {name|uc}!", new ModelPerson("Fred/Smith"));
		test("basic", "Hello {name|s|h|u}!", new ModelPerson("<em>Fred</em>"));
		test("basic", "{#friends}\n{name}, {age}{~n}\n{/friends}",
				new ModelPerson[] { new ModelPerson("Moe", 37),
						new ModelPerson("Larry", 39),
						new ModelPerson("Curly", 35) });
		test("basic",
				"{#friends}\n  {name}, {age}{~n}\n{:else}\n  You have no friends!\n{/friends}",
				new ModelPerson[] { new ModelPerson("Moe", 37),
						new ModelPerson("Larry", 39),
						new ModelPerson("Curly", 35) });
		test("basic",
				"{#friends}\n  {name}, {age}{~n}\n{:else}\n  You have no friends!\n{/friends}",
				new ModelPerson[] {});
		test("basic", "{#friends/}", new ModelPerson[] {});
		test("basic", "{#names}{.} {/names}", new ModelNames("Moe", "Larry", "Curly"));
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		map1.put("foo", map2);
		map2.put("bar", "It worked!");
		test("basic", "{foo.bar}", map1);
		map1 = new HashMap<String, Object>();
		map1.put("foo", "It worked!");
		test("basic", "{.foo}", map1);
		map1 = new HashMap<String, Object>();
		map1.put("profile", new ModelPerson("Fred"));
		test("basic", "{#profile bar=\"baz\" bing=\"bong\"}\n  {name}, {bar}, {bing}\n{/profile}", map1);
		map1 = new HashMap<String, Object>();
		map1.put("name", "Foo");
		map1.put("profile", new ModelPerson("Bar"));
		test("basic", "{name}{~n}\n{#profile root_name=name}\n  {name}, {root_name}\n{/profile}", map1);
		map1 = new HashMap<String, Object>();
		map1.put("name", "Fred");
		map1.put("snippet", new int[] { 1, 2, 3 });
		test("basic", "{#snippet id=\"{name}_id\"}\n  {id}\n{/snippet}", map1);
		map1 = new HashMap<String, Object>();
		map1.put("foo", "bar");
		map1.put("tags", new String[] { "a", "b", "c" });
		test("basic", "{?tags}\n  <ul class=\"{foo}\">\n    {#tags}\n      <li>{.}</li>\n    {/tags}\n  </ul>\n{:else}\n  No tags!\n{/tags}", map1);
		test("basic", "{#names}{.}{@idx}{.}{/idx}{@sep}, {/sep}{/names}", new ModelNames("Moe", "Larry", "Curly"));
		map1 = new HashMap<String, Object>();
		map1.put("list", new int[] { 1, 2, 3, 4, 5 });
		map1.put("projects", new ModelPerson[] { new ModelPerson("alpha"), new ModelPerson("beta"), new ModelPerson("omega") });
		test("basic", "{#list:projects}{name}{@sep}, {/sep}{/list}", map1);
		test("basic", "Hello{~n}World!", "");
		test("basic", "{!\n  Multiline\n  {#foo}{bar}{/foo}\n!}\n{!before!}Hello{!after!}", "");
		test("basic", "Start{~n}\n{+title}\n  Base Title\n{/title}\n{~n}\n{+main}\n  Base Content\n{/main}\n{~n}\nEnd", "");
		test("profile", "Start{~n}\n{+title}\n  {name}, {role}\n{/title}\nMiddle{~n}\n{+main}\n  {#degrees}{.}{@sep},{/sep}{/degrees}\n{/main}\nEnd", new ModelPerson("John Doe", "CEO", new String[] { "MBA", "PMP", "BS" }, new ModelPerson("Jane Doe", "CMO", new String[] { "BFA" }, "")));
		test("example", "{>profile/}", new ModelPerson("John Doe", "CEO", new String[] { "MBA", "PMP", "BS" }, new ModelPerson("Jane Doe", "CMO", new String[] { "BFA" }, "")));
		test("example", "{>profile:user/}", new ModelPerson("John Doe", "CEO", new String[] { "MBA", "PMP", "BS" }, new ModelPerson("Jane Doe", "CMO", new String[] { "BFA" }, "")));
		test("example", "{>profile/}\n{<title}\n  Hey there {name}!\n{/title}\n{<main}\n  I know you have all these fancy degrees, so hopefully\n  they make you a good {role}.\n{/main}", new ModelPerson("John Doe", "CEO", new String[] { "MBA", "PMP", "BS" }, new ModelPerson("Jane Doe", "CMO", new String[] { "BFA" }, "")));
		test("example", "{+block/}", "");
		dust.setContextPath(dust.getContextPath() + "/test");
		test("example", "{>\"~/templates/external.dust.html\"/}", new ModelPerson("John Doe", "CEO", new String[] { "MBA", "PMP", "BS" }, new ModelPerson("Jane Doe", "CMO", new String[] { "BFA" }, "")));
		map1 = new HashMap<String, Object>();
		map1.put("employees", new ModelPerson[] { new ModelPerson("John Doe", "CEO", new String[] { "MBA", "PMP", "BS" }, "foo"), new ModelPerson("Jane Doe", "CMO", new String[] { "BFA" }, "bar") });
		test("example", "{#employees}\n  {>\"~/templates/{type}.dust.html\"/}\n{/employees}", map1);
		map1 = new HashMap<String, Object>();
		map1.put("yes", "yes");
		test("example", "Can you see me? {no}", map1);
	}

}
