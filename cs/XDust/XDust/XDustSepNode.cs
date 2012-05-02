using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustSepNode : XDustNodeList, IXDustSectionNode
    {
        public XDustSepNode(IEnumerable<XDustNode> nodes)
            : base(nodes)
        {
            this.Parameters = new Dictionary<string, XDustNode>();
        }
        
        public Dictionary<String, XDustNode> Parameters { get; private set; }

        public override object PrepareModel(RenderChain chain, Context context, object model)
        {
            IScriptable scriptable = new Scriptable(model);
            Object sep = scriptable["@sep"];
            if (sep != null && (bool)sep)
            {
                return base.PrepareModel(chain, context, model);
            }
            else
            {
                return null;
            }
        }

        public override string ToString()
        {
            return String.Format("{{@sep}}{0}{{/sep}}", base.ToString());
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
