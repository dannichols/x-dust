using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security;
using System.Text;
using System.Text.RegularExpressions;

namespace XDust
{
    public class XDust
    {
        public XDust()
        {
            this.Parser = new XDustNodeListParser();
            this.Templates = new Dictionary<string, XDustTemplate>();
            this.Helpers = new Dictionary<String, IXDustHelper>();
            this.Filters = new Dictionary<string, Func<string, string>>()
            {
                {"s", s => s},
                {"h", s => this.EscapeHTML(s)},
                {"j", s => this.EscapeJS(s)},
                {"u", s => this.EscapeURI(s)},
                {"uc", s => this.EscapeURIComponent(s)}
            };
        }

        protected XDustNodeListParser Parser { get; set; }
        public Dictionary<String, XDustTemplate> Templates { get; private set; }
        public Dictionary<String, IXDustHelper> Helpers { get; private set; }
        public Dictionary<String, Func<String, String>> Filters { get; private set; }

        public String EscapeHTML(Object obj)
        {
            String str = obj == null ? String.Empty : obj.ToString();
            Regex exp = new Regex(@"[&<>\""]");
            if (!exp.IsMatch(str))
            {
                return str;
            }
            else
            {
                return SecurityElement.Escape(str);
            }
        }

        public String EscapeJS(Object obj)
        {
            String str = obj == null ? String.Empty : obj.ToString();
            return str.Replace(@"\", @"\\").Replace("\"", @"\""").Replace("'", @"\'").Replace("\r", @"\r").Replace("\u2028", @"\u2028").Replace("\u2029", @"\u2029").Replace("\n", @"\n").Replace("\f", @"\f").Replace("\t", @"\t");
        }

        public String EscapeURI(Object obj)
        {
            String str = obj == null ? String.Empty : obj.ToString();
            return Uri.EscapeUriString(str);
        }

        public String EscapeURIComponent(Object obj)
        {
            String str = obj == null ? String.Empty : obj.ToString();
            return Uri.EscapeDataString(str);
        }

        public XDustNode Compile(String str, String name, String sourceFile)
        {
            XDustNode rootNode = this.Parser.Parse(this, str);
            this.Templates[name] = new XDustTemplate(name, rootNode, sourceFile);
            return rootNode;
        }

        public XDustNode Load(String sourceFile, String name)
        {
            if (String.IsNullOrEmpty(name))
            {
                name = sourceFile;
            }
            else if (String.IsNullOrEmpty(sourceFile))
            {
                sourceFile = name;
            }
            if (this.Templates.ContainsKey(name))
            {
                return this.Templates[name].RootNode;
            }
            else
            {
                String code;
                using (FileStream fs = new FileStream(sourceFile, FileMode.Open))
                {
                    try
                    {
                        int length = (int)fs.Length;
                        byte[] buffer = new byte[length];
                        int count;
                        int sum = 0;
                        while ((count = fs.Read(buffer, sum, length - sum)) > 0)
                        {
                            sum += count;
                        }
                        code = UTF8Encoding.UTF8.GetString(buffer);
                    }
                    finally
                    {
                        fs.Close();
                    }
                }
                return this.Compile(code, name, sourceFile);
            }
        }

        public void Render(String name, Object model, Action<Exception, String> callback)
        {
            XDustTemplate template = this.Templates[name];
            String output = null;
            Exception error = null;
            try
            {
                output = template.Render(this, null, null, model);
            }
            catch (Exception e)
            {
                error = e;
            }
            callback.Invoke(error, output);
        }
    }
}
