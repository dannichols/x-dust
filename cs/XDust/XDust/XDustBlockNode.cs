using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustBlockNode : XDustNodeList
    {
        public XDustBlockNode(String name)
            : base(null)
        {
            this.Name = name;
        }

        public String Name { get; set; }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.Append("{+").Append(this.Name).Append("}");
            sb.Append(base.ToString());
            sb.Append("{/").Append(this.Name).Append("}");
            return sb.ToString();
        }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            XDustNode block = chain.GetBlock(this.Name);
            if (null != block)
            {
                return block.Render(dust, chain, context, model);
            }
            else
            {
                return base.Render(dust, chain, context, model);
            }
        }
    }
}
