using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Security;

namespace XDust
{
    public class XDustVariableNode : XDustNode
    {
        public XDustVariableNode(String path, IEnumerable<String> filters)
            : base()
        {
            this.Context = new ContextResolver(path);
            this.Filters = new List<String>();
            if (null != filters)
            {
                this.Filters.AddRange(filters);
            }
        }

        internal ContextResolver Context { get; set; }
        internal List<String> Filters { get; private set; }

        public override string ToString()
        {
            return String.Format("{{{0}{1}}}", this.Context, this.Filters.Count > 0 ? "|" + String.Join("|", this.Filters.ToArray()) : String.Empty);
        }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            chain = new RenderChain(chain, this);
            Object originalModel = model;
            model = this.Context.Resolve(context, model);
            String result = String.Empty;
            if (null != model)
            {
                if (model is XDustNode)
                {
                    result = ((XDustNode)model).Render(dust, chain, context, originalModel);
                }
                else if (model is ContextResolver)
                {
                    Object temp = ((ContextResolver)model).Resolve(context, originalModel);
                    if (null != temp)
                    {
                        result = temp.ToString();
                    }
                }
                else if (model is IScriptable)
                {
                    Object temp = ((IScriptable)model).Value;
                    if (null != temp)
                    {
                        result = temp.ToString();
                    }
                }
                else
                {
                    result = model.ToString();
                }
                if (this.Filters.Count > 0)
                {
                    foreach (String flag in this.Filters)
                    {
                        result = dust.Filters[flag].Invoke(result);
                    }
                }
                else
                {
                    SecurityElement.Escape(result);
                }
            }
            return result;
        }
    }
}
