/**
 * 该类中主要封装Extjs的一些方法，以达到主键脱离Extjs的依赖
 * 目前已经实现了以下方法
 * Ext.encode()
 * Ext.decode()
 * Ext.apply()
 * Ext.override()
 * Ext.extend()
 * Ext.isDefined()
 * Ext.isArray()
 * Ext.Date()
 * Ext.isString()
 * Ext.isBoolean()
 * Ext.isFunction()
 * Ext.id() 对该方法进行了简化
 */
com.doubeye.Ext = {
	__i : 0,
	id : function(){
		return  "doubeye-gen-" + (++ this.__i);
	},
	isDefined : function(object) {
		return typeof object !== 'undefined';
	},
	isArray : function(object) {
		return Object.prototype.toString.apply(object) === '[object Array]';
	},
	isDate : function(object) {
		return Object.prototype.toString.apply(object) === '[object Date]';
	},
	isString : function(object) {
		return typeof object === 'string';
	},
	isBoolean : function(v) {
		return typeof v === 'boolean';
	},
	isFunction : function(e) {
		//return u.apply(e) === "[object Function]"
		return typeof e === 'function';
	},
	isJSONObject : function(value) {
		return (typeof(value) == "object") &&
			(Object.prototype.toString.call(value).toLowerCase() == "[object object]") && (!value.length);
	},
    isJSONArray : function(value) {
        return (typeof(value) == "object") &&
            (!isNaN(value.length));
    },
	isJSON : function(value) {
		return com.doubeye.Ext.isJSONArray(value) || com.doubeye.Ext.isJSONObject(value);
	},
	pad : function(n) {
		return n < 10 ? "0" + n : n;
	},
	encodeArray : function(o) {
		var a = ["["], b, i, l = o.length, v;
		for ( i = 0; i < l; i += 1) {
			v = o[i];
			switch (typeof v) {
				case "undefined":
				case "function":
				case "unknown":
					break;
				default:
					if (b) {
						a.push(',');
					}
					//a.push(v === null ? "null" : Ext.util.JSON.encode(v));
					/**
					 * 如果发现有部分encode函数失效或不正常，回复上面一行并打开ext引用的注释 
					 */
					a.push(v === null ? "null" : com.doubeye.Ext.encode(v));
					b = true;
			}
		}
		a.push("]");
		return a.join("");
	},
	encodeDate : function(object) {
		return '"' + object.getFullYear() + "-" + this.pad(object.getMonth() + 1) + "-" + this.pad(object.getDate()) + "T" + this.pad(object.getHours()) + ":" + this.pad(object.getMinutes()) + ":" + this.pad(object.getSeconds()) + '"';
	},
	encodeString : function(s) {
		var m = {
			"\b" : '\\b',
			"\t" : '\\t',
			"\n" : '\\n',
			"\f" : '\\f',
			"\r" : '\\r',
			'"' : '\\"',
			"\\" : '\\\\'
		};
		if (/["\\\x00-\x1f]/.test(s)) {
			return '"' + s.replace(/([\x00-\x1f\\"])/g, function(a, b) {
				var c = m[b];
				if (c) {
					return c;
				}
				c = b.charCodeAt();
				return "\\u00" + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
			}) + '"';
		}
		return '"' + s + '"';
	},
	encode : function(o) {
		var useHasOwn = !! {}.hasOwnProperty;
		if (!this.isDefined(o) || o === null) {
			return "null";
		} else if (this.isArray(o)) {
			return this.encodeArray(o);
		} else if (this.isDate(o)) {
			return this.encodeDate(o);
		} else if (this.isString(o)) {
			return this.encodeString(o);
		} else if ( typeof o == "number") {
			//don't use isNumber here, since finite checks happen inside isNumber
			return isFinite(o) ? String(o) : "null";
		} else if (this.isBoolean(o)) {
			return String(o);
		} else {
			var a = ["{"], b, i, v;
			for (i in o) {
				if (!o.getElementsByTagName) {
					if (!useHasOwn || o.hasOwnProperty(i)) {
						v = o[i];
						switch (typeof v) {
							case "undefined":
							case "function":
							case "unknown":
								break;
							default:
								if (b) {
									a.push(',');
								}
								a.push(this.encode(i), ":", v === null ? "null" : this.encode(v));
								b = true;
						}
					}
				}
			}
			a.push("}");
			return a.join("");
		}
	},
	decode : function(json) {
		return json ? eval("(" + json + ")") : "";
	},
	/**
	 * 复制config对象的所有属性到obj（第一个参数为obj，第二个参数为config）。
	 * @param {Object} 属性接受方对象
	 * @param {Object} 属性源对象
	 * @param {Object} 默认对象，如果该参数存在，obj将会得到那些defaults有而config没有的属性。
	 * @return {Object} returns obj
	 */
	apply : function(obj, config, defaults) {
		// no "this" reference for friendly out of scope calls
		if (defaults) {
			com.doubeye.Ext.apply(obj, defaults);
		}
		if (obj && config && typeof config == 'object') {
			for (var p in config) {
				obj[p] = config[p];
			}
		}
		return obj;
	},
	override : function(origclass, overrides) {
		if (overrides) {
			var p = origclass.prototype;
			com.doubeye.Ext.apply(p, overrides);
			if (/msie/.test(navigator.userAgent.toLowerCase()) && overrides.hasOwnProperty('toString')) {
				p.toString = overrides.toString;
			}
		}
	},
	extend : function() {
		// inline overrides
		var io = function(o) {
			for (var m in o) {
				this[m] = o[m];
			}
		};
		var oc = Object.prototype.constructor;
		return function(sb, sp, overrides) {
			if ( typeof sp == 'object') {
				overrides = sp;
				sp = sb;
				sb = overrides.constructor != oc ? overrides.constructor : function() {
					sp.apply(this, arguments);
				};
			}
			var F = function() {
			}, sbp, spp = sp.prototype;

			F.prototype = spp;
			sbp = sb.prototype = new F();
			sbp.constructor = sb;
			sb.superclass = spp;
			if (spp.constructor == oc) {
				spp.constructor = sp;
			}
			sb.override = function(o) {
				com.doubeye.Ext.override(sb, o);
			};
			sbp.superclass = sbp.supr = (function() {
				return spp;
			});
			sbp.override = io;
			com.doubeye.Ext.override(sb, overrides);
			sb.extend = function(o) {
				return com.doubeye.Ext.extend(sb, o);
			};
			return sb;
		};
	}()
}; 