using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class RenderChain
    {
        public RenderChain(RenderChain head, XDustNode tail)
        {
            this.Head = head;
            this.Tail = tail;
        }

        public RenderChain Head { get; private set; }
        public XDustNode Tail { get; private set; }

        public XDustNode GetBlock(String name)
        {
            var block = this.Tail.GetBlock(name);
            if (null != block)
            {
                return block;
            }
            else if (null != this.Head)
            {
                return this.Head.GetBlock(name);
            }
            else
            {
                return null;
            }
        }
    }
}
