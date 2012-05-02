using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public interface IXDustHelper
    {
        String Render(XDust dust, RenderChain chain, Context context, Object model);
    }
}
