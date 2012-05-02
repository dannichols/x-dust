using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustTextNode : XDustNode
    {
        public XDustTextNode(String value)
            : base()
        {
            this.Buffer = new StringBuilder();
            if (null != value)
            {
                this.Buffer.Append(value);
            }
        }

        private StringBuilder Buffer { get; set; }
        private String Value { get; set; }

        public void Write(Object value)
        {
            this.Buffer.Append(value);
        }

        public void Close()
        {
            if (null != this.Buffer)
            {
                this.Value = this.Buffer.ToString();
                this.Buffer = null;
            }
        }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            this.Close();
            return this.Value;
        }

        public override string ToString()
        {
            this.Close();
            return this.Value;
        }
    }
}
