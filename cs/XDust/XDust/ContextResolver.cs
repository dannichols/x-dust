using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class ContextResolver
    {
        public ContextResolver(String path)
        {
            this.Path = new List<string>();
            if (path.StartsWith("."))
            {
                this.Path.Add(".");
                this.Path.AddRange(path.Split('.').Skip(1));
            }
            else
            {
                this.Path.AddRange(path.Split('.'));
            }
        }

        public List<String> Path { get; private set; }

        public override String ToString()
        {
            var path = new List<String>(this.Path);
            if (path[0] == ".")
            {
                path[0] = String.Empty;
            }
            return String.Join(".", path.ToArray());
        }

        public Object Resolve(Context context, Object model)
        {
            Object copy = new Context(context, model, null);
            if (null != model)
            {
                if (model is IEnumerable<Object>)
                {
                    copy = new List<Object>((IEnumerable<Object>)model);
                }
                else
                {
                    foreach (var segment in this.Path)
                    {
                        var ctx = (Context)copy;
                        if (!String.IsNullOrEmpty(segment))
                        {
                            if (segment == ".")
                            {
                                if (ctx.Tail.IsValue)
                                {
                                    copy = ctx.Tail.Value;
                                    break;
                                }
                                else
                                {
                                    ctx.Head = null;
                                }
                            }
                            else
                            {
                                var value = ctx.Get(segment);
                                if (value != null)
                                {
                                    ctx = new Context(copy, value, null);
                                    if (ctx.Tail.IsValue)
                                    {
                                        copy = ctx.Tail;
                                    }
                                    else
                                    {
                                        copy = ctx;
                                    }
                                }
                                else
                                {
                                    copy = null;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            copy = ctx.Tail;
                            break;
                        }
                    }
                }
            }
            else
            {
                copy = null;
            }
            return copy;
        }
    }
}
