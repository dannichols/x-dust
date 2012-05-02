using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;

namespace XDust
{
    public class Scriptable : IScriptable
    {
        public static IScriptable From(Object obj)
        {
            if (null != obj && obj is IScriptable)
            {
                return (IScriptable)obj;
            }
            else
            {
                return new Scriptable(obj);
            }
        }

        private Object target;
        private Boolean targetIsValue;
        private Boolean targetIsDict;
        private Boolean targetIsNull;
        private Boolean targetIsContext;
        private Boolean targetIsEnumerable;

        private Scriptable(Object target)
        {
            if (null == target)
            {
                this.target = null;
                this.targetIsDict = false;
                this.targetIsContext = false;
                this.targetIsValue = true;
                this.targetIsNull = true;
                this.targetIsEnumerable = false;
            }
            else
            {
                this.target = target;
                this.targetIsContext = target is Context;
                this.targetIsDict = target is Dictionary<String, Object>;
                this.targetIsEnumerable = !this.targetIsDict && target is IEnumerable<Object>;
                this.targetIsValue = this.targetIsEnumerable
                    || target is int
                    || target is string
                    || target is double
                    || target is bool
                    || target is decimal
                    || target is long
                    || target is float
                    || target is char
                    || target is byte
                    || target is sbyte
                    || target is uint
                    || target is short
                    || target is ushort
                    || target is ulong;
            }
        }

        public Object Value
        {
            get
            {
                if (this.targetIsValue)
                {
                    return this.target;
                }
                else if (this.targetIsContext)
                {
                    var ctx = (Context)this.target;
                    return ctx.Tail == null ? null : ctx.Tail.Value;
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
                if (this.targetIsContext && ((Context)this.target).Tail != null)
                {
                    return ((Context)this.target).Tail.IsValue;
                }
                else
                {
                    return this.targetIsValue;
                }
            }
        }

        public bool IsEnumerable
        {
            get
            {
                return this.targetIsEnumerable;
            }
        }

        public IEnumerable<Object> AsEnumerable
        {
            get
            {
                return (IEnumerable<Object>)this.target;
            }
        }

        public bool IsTruthy
        {
            get
            {
                if (this.targetIsNull)
                {
                    return false;
                }
                else if (this.targetIsEnumerable)
                {
                    return this.AsEnumerable.Count() > 0;
                }
                else if (this.targetIsDict)
                {
                    return ((Dictionary<String, Object>)this.target).Count() > 0;
                }
                else if (this.targetIsValue)
                {
                    if (this.target is String)
                    {
                        return !String.IsNullOrEmpty((String)this.target);
                    }
                    else if (this.target is int || this.target is double || this.target is float || this.target is decimal || this.target is uint || this.target is ushort)
                    {
                        return (double)this.target != 0;
                    }
                    else if (this.target is long)
                    {
                        return (long)this.target != 0;
                    }
                    else
                    {
                        return true;
                    }
                }
                else
                {
                    return true;
                }
            }
        }

        public Object this[String name]
        {
            get
            {
                if (this.targetIsContext)
                {
                    return ((Context)this.target).Get(name);
                }
                else if (this.targetIsNull || this.targetIsValue)
                {
                    return null;
                }
                else if (this.targetIsDict)
                {
                    return ((Dictionary<String, Object>)this.target)[name];
                }
                else
                {
                    Type T = this.target.GetType();
                    PropertyInfo p = T.GetProperty(name);
                    if (null == p)
                    {
                        return null;
                    }
                    else
                    {
                        return p.GetValue(this.target, null);
                    }
                }
            }
        }

        public bool HasKey(String name)
        {
            if (this.targetIsNull || this.targetIsValue)
            {
                return false;
            }
            else if (this.targetIsDict)
            {
                return ((Dictionary<String, Object>)this.target).ContainsKey(name);
            }
            else if (this.targetIsContext)
            {
                return ((Context)this.target).Get(name) != null;
            }
            else
            {
                Type T = this.target.GetType();
                PropertyInfo p = T.GetProperty(name);
                return null != p;
            }
        }

        #region IEnumerable<string> Members

        public IEnumerator<string> GetEnumerator()
        {
            if (this.targetIsNull || this.targetIsValue)
            {
                return null;
            }
            else if (this.targetIsDict)
            {
                return ((Dictionary<String, Object>)this.target).Keys.GetEnumerator();
            }
            else
            {
                Type T = this.target.GetType();
                IEnumerable<String> names = T.GetProperties().Select(p => p.Name);
                return names.GetEnumerator();
            }
        }

        #endregion

        #region IEnumerable Members

        System.Collections.IEnumerator System.Collections.IEnumerable.GetEnumerator()
        {
            return this.GetEnumerator();
        }

        #endregion

        public override string ToString()
        {
            if (null == this.target)
            {
                return String.Empty;
            }
            else
            {
                return this.target.ToString();
            }
        }
    }
}
