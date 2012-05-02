using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustInlinePartialNode : XDustNodeList, IXDustSectionNode
    {
        public XDustInlinePartialNode(String name)
            : base(null)
        {
            this.Name = name;
            this.Parameters = new Dictionary<string, XDustNode>();
        }

        public String Name { get; set; }
        public Dictionary<String, XDustNode> Parameters { get; private set; }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("{<").Append(this.Name).Append("}");
            sb.Append(base.ToString());
            sb.Append("{/").Append(this.Name).Append("}");
            return sb.ToString();
        }

        public override XDustNode AsBlock()
        {
            return new XDustNodeList(this.Nodes);
        }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            return String.Empty;
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
