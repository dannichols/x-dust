using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustExistsNode : XDustLogicNode
    {
        public XDustExistsNode(String path, String scope, Dictionary<String, XDustNode> parameters)
            : base(path, scope, parameters)
        {
        }

        public override string Operator
        {
            get
            {
                return "?";
            }
        }

        public override bool AllowIteration
        {
            get
            {
                return false;
            }
        }

        public override Context PrepareModel(XDust dust, RenderChain chain, Context context, object model)
        {
            return new Context(context, context, this.Parameters.ToDictionary(kvp => kvp.Key, kvp => kvp.Value as Object));
        }
    }
}
