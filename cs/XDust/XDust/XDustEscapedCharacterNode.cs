using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustEscapedCharacterNode : XDustNode
    {

        public XDustEscapedCharacterNode(String code)
            : base()
        {
            this.Code = code;
            switch (code)
            {
                case "n":
                    this.Character = "\n";
                    break;
                case "r":
                    this.Character = "\r";
                    break;
                case "s":
                    this.Character = " ";
                    break;
                case "lb":
                    this.Character = "{";
                    break;
                case "rb":
                    this.Character = "}";
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }
        }

        public String Code { get; private set; }
        public String Character { get; private set; }

        public override string ToString()
        {
            return String.Format("{{~{0}}}", this.Code);
        }

        public override String Render(XDust dust, RenderChain chain, Context context, object model)
        {
            return this.Character;
        }
    }
}
