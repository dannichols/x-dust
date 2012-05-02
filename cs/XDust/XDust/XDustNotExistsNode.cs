using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustNotExistsNode : XDustExistsNode
    {
        public XDustNotExistsNode(String path, String scope, Dictionary<String, XDustNode> parameters)
            : base(path, scope, parameters)
        {
        }

        public override string ChooseBodyName(Context context, object model)
        {
            Object resolved = this.Context.Resolve(context, model);
            return this.IsTruthy(resolved) ? XDustLogicNode.ELSE : XDustLogicNode.BLOCK;
        }
    }
}
