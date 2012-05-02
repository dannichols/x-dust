using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustPartialNode : XDustNode, IXDustSectionNode
    {
        public XDustPartialNode(Object include, String scope)
            : base()
        {
            this.Include = include;
            if (null != scope)
            {
                this.Scope = new ContextResolver(scope);
            }
            this.Parameters = new Dictionary<string, XDustNode>();
        }

        public Object Include { get; set; }
        public ContextResolver Scope { get; set; }
        public Dictionary<String, XDustNode> Parameters { get; private set; }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder("{>");
            if (this.Include is XDustNodeList)
            {
                sb.Append('"');
            }
            sb.Append(this.Include);
            if (this.Include is XDustNodeList)
            {
                sb.Append('"');
            }
            if (null != this.Scope)
            {
                sb.Append(":").Append(this.Scope);
            }
            sb.Append("/}");
            return sb.ToString();
        }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            if (null != this.Scope)
            {
                model = this.Scope.Resolve(context, model);
            }
            chain = new RenderChain(chain, this);
            String name;
            if (this.Include is XDustNodeList)
            {
                name = ((XDustNodeList)this.Include).Render(dust, chain, context, model);
            }
            else
            {
                name = this.Include.ToString();
            }
            XDustNode template = dust.Load(null, name);
            return template.Render(dust, chain, context, model);
        }

        #region IXDustSectionNode Members


        public XDustNodeList StartBody(string name)
        {
            throw new NotImplementedException();
        }

        public void EndBody()
        {
            throw new NotImplementedException();
        }

        #endregion
    }
}
