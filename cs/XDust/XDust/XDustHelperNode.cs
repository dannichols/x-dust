using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustHelperNode : XDustLogicNode
    {
        public override bool AllowIteration
        {
            get
            {
                return false;
            }
        }

        public XDustHelperNode(String name, String scope, Dictionary<String, XDustNode> parameters)
            : base(null, scope, parameters)
        {
            this.Name = name;
        }

        public String Name { get; set; }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            chain = new RenderChain(chain, this);
            context = new Context(context, null, this.Parameters.ToDictionary(kvp => kvp.Key, kvp => kvp.Value as Object));
            var helper = dust.Helpers[this.Name];
            return helper.Render(dust, chain, context, model);
        }
    }
}
