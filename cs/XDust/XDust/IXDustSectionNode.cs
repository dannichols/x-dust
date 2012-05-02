using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public interface IXDustSectionNode
    {
        Dictionary<String, XDustNode> Parameters { get; }

        XDustNodeList StartBody(String name);

        void EndBody();
    }
}
