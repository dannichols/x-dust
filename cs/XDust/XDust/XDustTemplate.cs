using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustTemplate
    {
        public XDustTemplate(String name, XDustNode rootNode, String sourceFile)
        {
            this.Name = name;
            this.RootNode = rootNode;
            this.SourceFile = sourceFile;
        }

        public String Name { get; private set; }
        public XDustNode RootNode { get; private set; }
        public String SourceFile { get; private set; }

        public String Render(XDust dust, RenderChain chain, Context context, Object model)
        {
            return this.RootNode.Render(dust, chain, context, model);
        }
    }
}
