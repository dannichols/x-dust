using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public interface IScriptable : IEnumerable<String>
    {
        Object Value { get; }
        bool IsValue { get; }
        Object this[String name] { get; }
        bool HasKey(String name);
    }
}
