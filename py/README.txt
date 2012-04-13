Calling dust.py

To import dust.py:

from dust import dust

To compile a template from a string:

dust.compile(template_code, name)

...where template_code is the raw dust template string and name is the key by which the resulting template can be referenced.

To load and compile a template from a local file:

dust.load(src_file_path, name)

...where src_file_path is the path to the text file containing the dust template code and name is the key by which the resulting template can be referenced.

To render a template:

dust.render(name, model, callback)

...where name is the name of the template, model is a dict of data to feed the template, and callback is a function taking two arguments, an exception and the string output.

If an error occurs while rendering, the first argument to callback will be the exception that occurred, otherwise it will be None.

If the render completes successfully, the second argument to callback will be the string output, otherwise it will be None.