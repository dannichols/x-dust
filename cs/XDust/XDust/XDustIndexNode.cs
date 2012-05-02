using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustIndexNode : XDustNodeList, IXDustSectionNode
    {
        public XDustIndexNode(IEnumerable<XDustNode> nodes)
            : base(nodes)
        {
            this.Parameters = new Dictionary<string, XDustNode>();
        }

        public override Object PrepareModel(RenderChain chain, Context context, Object model)
        {
            IScriptable scriptable = new Scriptable(model);
            if (scriptable["@idx"] != null)
            {
                return scriptable["@idx"].ToString();
            }
            else
            {
                return null;
            }
        }

        public Dictionary<String, XDustNode> Parameters { get; private set; }

        public override string ToString()
        {
            return String.Format("{{@idx}}{0}{{/idx}}", base.ToString());
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
