using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustNodeList : XDustNode, IEnumerable<XDustNode>
    {
        public XDustNodeList(IEnumerable<XDustNode> nodes)
            : base()
        {
            this.Nodes = new List<XDustNode>();
            if (null != nodes)
            {
                this.Nodes.AddRange(nodes);
            }
        }

        public List<XDustNode> Nodes { get; private set; }

        public XDustNode Add(XDustNode node)
        {
            this.Nodes.Add(node);
            return node;
        }

        public XDustNode Last()
        {
            return this.Nodes.Last();
        }

        public virtual Object PrepareModel(RenderChain chain, Context context, Object model)
        {
            return new Context(context, model, null);
        }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            chain = new RenderChain(chain, this);
            model = this.PrepareModel(chain, context, model);
            StringBuilder sb = new StringBuilder();
            if (null != model)
            {
                foreach (var node in this.Nodes)
                {
                    sb.Append(node.Render(dust, chain, context, model));
                }
            }
            return sb.ToString();
        }

        public override string ToString()
        {
            return String.Join(String.Empty, this.Nodes.Select(n => n.ToString()).ToArray());
        }

        #region IEnumerable<XDustNode> Members

        public IEnumerator<XDustNode> GetEnumerator()
        {
            return this.Nodes.GetEnumerator();
        }

        #endregion

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion
    }
}
