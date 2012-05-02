using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace XDust
{
    public class XDustLogicNode : XDustNode, IXDustSectionNode
    {
        protected static readonly String BLOCK = "block";
        protected static readonly String ELSE = "else";

        public virtual String Operator
        {
            get
            {
                return "#";
            }
        }

        public virtual bool AllowIteration
        {
            get
            {
                return true;
            }
        }

        public XDustLogicNode(String path, String scope, Dictionary<String, XDustNode> parameters)
            : base()
        {
            this.Bodies = new Dictionary<String, XDustNodeList>();
            this.StartBody(XDustLogicNode.BLOCK);
            this.Parameters = new Dictionary<String, XDustNode>();
            if (null != parameters)
            {
                foreach (var kvp in parameters)
                {
                    this.Parameters[kvp.Key] = kvp.Value;
                }
            }
            if (!String.IsNullOrEmpty(path))
            {
                this.Context = new ContextResolver(path);
            }
            if (!String.IsNullOrEmpty(scope))
            {
                this.Scope = new ContextResolver(scope);
            }
        }

        public Dictionary<String, XDustNodeList> Bodies { get; private set; }
        public Dictionary<String, XDustNode> Parameters { get; private set; }
        public ContextResolver Context { get; private set; }
        public ContextResolver Scope { get; private set; }
        public XDustNodeList CurrentBody { get; private set; }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder("{");
            sb.Append(this.Operator).Append(this.Context);
            if (null != this.Scope)
            {
                sb.Append(":").Append(this.Scope);
            }
            foreach (var kvp in this.Parameters)
            {
                sb.Append(" ").Append(kvp.Key).Append("=");
                if (kvp.Value is XDustNodeList)
                {
                    sb.Append('"').Append(kvp.Value).Append('"');
                }
                else if (kvp.Value is XDustVariableNode)
                {
                    sb.Append(((XDustVariableNode)kvp.Value).Context);
                }
                else if (kvp.Value is XDustTextNode)
                {
                    sb.Append('"').Append(kvp.Value).Append('"');
                }
                else
                {
                    sb.Append(kvp.Value);
                }
            }
            sb.Append("}").Append(this.Bodies[XDustLogicNode.BLOCK]);
            foreach (var kvp in this.Bodies.Where(kvp => kvp.Key != XDustLogicNode.BLOCK))
            {
                sb.Append("{:").Append(kvp.Key).Append("}").Append(kvp.Value);
            }
            sb.Append("{/").Append(this.Context).Append("}");
            return sb.ToString();
        }

        public XDustNodeList StartBody(String name)
        {
            var body = new XDustNodeList(null);
            this.CurrentBody = body;
            this.Bodies[name] = body;
            return body;
        }

        public void EndBody()
        {
            this.CurrentBody = null;
        }

        public virtual Context PrepareModel(XDust dust, RenderChain chain, Context context, Object model)
        {
            var parameters = new Dictionary<String, Object>();
            if (null != this.Parameters)
            {
                foreach (var kvp in this.Parameters)
                {
                    parameters[kvp.Key] = kvp.Value.Render(dust, chain, context, context);
                }
            }
            Context result = new Context(context, model, parameters);
            return result;
        }

        public virtual String RenderBody(XDust dust, String name, RenderChain chain, Context context, Object model)
        {
            StringBuilder sb = new StringBuilder();
            XDustNodeList body = this.Bodies.ContainsKey(name) ? this.Bodies[name] : null;
            context = new Context(context, model, this.Parameters.ToDictionary(kvp => kvp.Key, kvp => kvp.Value as Object));
            if (null != this.Scope)
            {
                model = this.Scope.Resolve(context, model);
            }
            else if (null != this.Context)
            {
                model = this.Context.Resolve(context, model);
            }
            if (null != body)
            {
                chain = new RenderChain(chain, body);
                if (this.AllowIteration && (model is IEnumerable<Object> || (model is Scriptable && ((Scriptable)model).IsEnumerable)))
                {
                    var list = model is IEnumerable<Object> ? (IEnumerable<Object>)model : ((Scriptable)model).AsEnumerable;
                    var length = list.Count();
                    if (name == XDustLogicNode.ELSE && length < 1)
                    {
                        sb.Append(body.Render(dust, chain, context, null));
                    }
                    else
                    {
                        for (var i = 0; i < length; i++)
                        {
                            Context iterModel = this.PrepareModel(dust, chain, context, list.ElementAt(i));
                            iterModel.Parameters["@idx"] = i;
                            iterModel.Parameters["@sep"] = i != length - 1;
                            sb.Append(body.Render(dust, chain, context, iterModel));
                        }
                    }
                }
                else
                {
                    Context iterModel = this.PrepareModel(dust, chain, context, model);
                    sb.Append(body.Render(dust, chain, context, iterModel));
                }
            }
            return sb.ToString();
        }

        protected bool IsTruthy(Object obj)
        {
            if (null == obj)
            {
                return false;
            }
            else if (obj is IEnumerable<Object> && ((IEnumerable<Object>)obj).Count() < 1)
            {
                return false;
            }
            else if (obj is Boolean && !(Boolean)obj)
            {
                return false;
            }
            else if (obj is String && String.IsNullOrEmpty((String)obj))
            {
                return false;
            }
            else if (obj is Scriptable)
            {
                return ((Scriptable)obj).IsTruthy;
            }
            else
            {
                return true;
            }
        }

        public virtual String ChooseBodyName(Context context, Object model)
        {
            Object resolved = this.Context.Resolve(context, model);
            return this.IsTruthy(resolved) ? XDustLogicNode.BLOCK : XDustLogicNode.ELSE;
        }

        public override string Render(XDust dust, RenderChain chain, Context context, object model)
        {
            chain = new RenderChain(chain, this);
            context = new Context(context, null, this.Parameters.ToDictionary(kvp => kvp.Key, kvp => kvp.Value as Object));
            var bodyName = this.ChooseBodyName(context, model);
            return this.RenderBody(dust, bodyName, chain, context, model);
        }
    }
}
