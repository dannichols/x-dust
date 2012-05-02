using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public abstract class XDustNode
    {
        public XDustNode()
        {
            this.Blocks = new Dictionary<String, XDustNode>();
        }

        public Dictionary<String, XDustNode> Blocks { get; private set; }

        public abstract String Render(XDust dust, RenderChain chain, Context context, Object model);

        public XDustNode GetBlock(String name)
        {
            if (this.HasBlock(name))
            {
                return this.Blocks[name].AsBlock();
            }
            else
            {
                return null;
            }
        }

        public void SetBlock(String name, XDustNode value)
        {
            this.Blocks[name] = value;
        }

        public bool HasBlock(String name)
        {
            return this.Blocks.ContainsKey(name);
        }

        public virtual XDustNode AsBlock()
        {
            return this;
        }
    }
}
