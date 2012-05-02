using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class Context : IEnumerable<String>, IScriptable
    {
        public Context(Object head, Object tail, Dictionary<String, Object> parameters)
        {
            this.Parameters = new Dictionary<String, Object>();
            if (null != parameters)
            {
                foreach (var name in parameters.Keys)
                {
                    this.Parameters[name] = parameters[name];
                }
            }
            this.Tail = Scriptable.From(tail);
        }

        public Context Head { get; set; }
        public IScriptable Tail { get; set; }
        public Dictionary<String, Object> Parameters { get; private set; }

        public Object this[String name]
        {
            get
            {
                return this.Get(name);
            }
        }

        public Object Get(String name)
        {
            if (name == ".")
            {
                return this.Value;
            }
            else if (this.Tail.HasKey(name))
            {
                return this.Tail[name];
            }
            else if (this.Parameters.ContainsKey(name))
            {
                return this.Parameters[name];
            }
            else if (null != this.Head)
            {
                return this.Head.Get(name);
            }
            else
            {
                return null;
            }
        }

        public void Update(Object other)
        {
            if (null != other)
            {
                this.Tail = Scriptable.From(other);
                if (other is Context)
                {
                    foreach (var param in ((Context)other).Parameters)
                    {
                        this.Parameters[param.Key] = param.Value;
                    }
                }
            }
        }

        #region IEnumerable<string> Members

        public IEnumerator<String> GetEnumerator()
        {
            if (this.Tail.IsValue)
            {
                throw new InvalidOperationException();
            }
            else
            {
                return this.Tail.GetEnumerator();
            }
        }

        #endregion

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion

        #region IEnumerable<string> Members

        IEnumerator<string> IEnumerable<string>.GetEnumerator()
        {
            throw new NotImplementedException();
        }

        #endregion

        #region IScriptable Members

        public object Value
        {
            get
            {
                if (null != this.Tail)
                {
                    return this.Tail.Value;
                }
                else if (null != this.Head)
                {
                    return this.Head.Value;
                }
                else
                {
                    return null;
                }
            }
        }

        public bool IsValue
        {
            get
            {
                if (null != this.Tail)
                {
                    return this.Tail.IsValue;
                }
                else if (null != this.Head)
                {
                    return this.Head.IsValue;
                }
                else
                {
                    return false;
                }
            }
        }

        public bool HasKey(string name)
        {
            if (null != this.Parameters && this.Parameters.ContainsKey(name))
            {
                return true;
            }
            else if (null != this.Tail && this.Tail.HasKey(name))
            {
                return true;
            }
            else if (null != this.Head && this.Head.HasKey(name))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        #endregion
    }
}
