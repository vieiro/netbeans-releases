/*
Copyright (c) 2006, Yahoo! Inc. All rights reserved.
Code licensed under the BSD License:
http://developer.yahoo.net/yui/license.txt
version: 0.10.0
*/
YAHOO.util.Anim=function(el,attributes,duration,method){if(el){this.init(el,attributes,duration,method);}};YAHOO.util.Anim.prototype={doMethod:function(attribute,start,end){return this.method(this.currentFrame,start,end-start,this.totalFrames);},setAttribute:function(attribute,val,unit){YAHOO.util.Dom.setStyle(this.getEl(),attribute,val+unit);},getAttribute:function(attribute){return parseFloat(YAHOO.util.Dom.getStyle(this.getEl(),attribute));},defaultUnit:'px',defaultUnits:{opacity:' '},init:function(el,attributes,duration,method){var isAnimated=false;var startTime=null;var endTime=null;var actualFrames=0;var defaultValues={};el=YAHOO.util.Dom.get(el);this.attributes=attributes||{};this.duration=duration||1;this.method=method||YAHOO.util.Easing.easeNone;this.useSeconds=true;this.currentFrame=0;this.totalFrames=YAHOO.util.AnimMgr.fps;this.getEl=function(){return el;};this.setDefault=function(attribute,val){if(val.constructor!=Array&&(val=='auto'||isNaN(val))){switch(attribute){case'width':val=el.clientWidth||el.offsetWidth;break;case'height':val=el.clientHeight||el.offsetHeight;break;case'left':if(YAHOO.util.Dom.getStyle(el,'position')=='absolute'){val=el.offsetLeft;}else{val=0;}break;case'top':if(YAHOO.util.Dom.getStyle(el,'position')=='absolute'){val=el.offsetTop;}else{val=0;}break;default:val=0;}}defaultValues[attribute]=val;};this.getDefault=function(attribute){return defaultValues[attribute];};this.isAnimated=function(){return isAnimated;};this.getStartTime=function(){return startTime;};this.animate=function(){if(this.isAnimated()){return false;}this.onStart.fire();this._onStart.fire();this.totalFrames=(this.useSeconds)?Math.ceil(YAHOO.util.AnimMgr.fps*this.duration):this.duration;YAHOO.util.AnimMgr.registerElement(this);var attributes=this.attributes;var el=this.getEl();var val;for(var attribute in attributes){val=this.getAttribute(attribute);this.setDefault(attribute,val);}isAnimated=true;actualFrames=0;startTime=new Date();};this.stop=function(){if(!this.isAnimated()){return false;}this.currentFrame=0;endTime=new Date();var data={time:endTime,duration:endTime-startTime,frames:actualFrames,fps:actualFrames/this.duration};isAnimated=false;actualFrames=0;this.onComplete.fire(data);};var onTween=function(){var start;var end=null;var val;var unit;var attributes=this['attributes'];for(var attribute in attributes){unit=attributes[attribute]['unit']||this.defaultUnits[attribute]||this.defaultUnit;if(typeof attributes[attribute]['from']!='undefined'){start=attributes[attribute]['from'];}else{start=this.getDefault(attribute);}if(typeof attributes[attribute]['to']!='undefined'){end=attributes[attribute]['to'];}else if(typeof attributes[attribute]['by']!='undefined'){if(start.constructor==Array){end=[];for(var i=0,len=start.length;i<len;++i){end[i]=start[i]+attributes[attribute]['by'][i];}}else{end=start+attributes[attribute]['by'];}}if(end!==null&&typeof end!='undefined'){val=this.doMethod(attribute,start,end);if((attribute=='width'||attribute=='height'||attribute=='opacity')&&val<0){val=0;}this.setAttribute(attribute,val,unit);}}actualFrames+=1;};this._onStart=new YAHOO.util.CustomEvent('_onStart',this);this.onStart=new YAHOO.util.CustomEvent('start',this);this.onTween=new YAHOO.util.CustomEvent('tween',this);this._onTween=new YAHOO.util.CustomEvent('_tween',this);this.onComplete=new YAHOO.util.CustomEvent('complete',this);this._onTween.subscribe(onTween);}};YAHOO.util.AnimMgr=new function(){var thread=null;var queue=[];var tweenCount=0;this.fps=200;this.delay=1;this.registerElement=function(tween){if(tween.isAnimated()){return false;}queue[queue.length]=tween;tweenCount+=1;this.start();};this.start=function(){if(thread===null){thread=setInterval(this.run,this.delay);}};this.stop=function(tween){if(!tween){clearInterval(thread);for(var i=0,len=queue.length;i<len;++i){if(queue[i].isAnimated()){queue[i].stop();}}queue=[];thread=null;tweenCount=0;}else{tween.stop();tweenCount-=1;if(tweenCount<=0){this.stop();}}};this.run=function(){for(var i=0,len=queue.length;i<len;++i){var tween=queue[i];if(!tween||!tween.isAnimated()){continue;}if(tween.currentFrame<tween.totalFrames||tween.totalFrames===null){tween.currentFrame+=1;if(tween.useSeconds){correctFrame(tween);}tween.onTween.fire();tween._onTween.fire();}else{YAHOO.util.AnimMgr.stop(tween);}}};var correctFrame=function(tween){var frames=tween.totalFrames;var frame=tween.currentFrame;var expected=(tween.currentFrame*tween.duration*1000/tween.totalFrames);var elapsed=(new Date()-tween.getStartTime());var tweak=0;if(elapsed<tween.duration*1000){tweak=Math.round((elapsed/expected-1)*tween.currentFrame);}else{tweak=frames-(frame+1);}if(tweak>0&&isFinite(tweak)){if(tween.currentFrame+tweak>=frames){tweak=frames-(frame+1);}tween.currentFrame+=tweak;}};};YAHOO.util.Bezier=new function(){this.getPosition=function(points,t){var n=points.length;var tmp=[];for(var i=0;i<n;++i){tmp[i]=[points[i][0],points[i][1]];}for(var j=1;j<n;++j){for(i=0;i<n-j;++i){tmp[i][0]=(1-t)*tmp[i][0]+t*tmp[parseInt(i+1,10)][0];tmp[i][1]=(1-t)*tmp[i][1]+t*tmp[parseInt(i+1,10)][1];}}return[tmp[0][0],tmp[0][1]];};};YAHOO.util.Easing=new function(){this.easeNone=function(t,b,c,d){return b+c*(t/=d);};this.easeIn=function(t,b,c,d){return b+c*((t/=d)*t*t);};this.easeOut=function(t,b,c,d){var ts=(t/=d)*t;var tc=ts*t;return b+c*(tc+-3*ts+3*t);};this.easeBoth=function(t,b,c,d){var ts=(t/=d)*t;var tc=ts*t;return b+c*(-2*tc+3*ts);};this.backIn=function(t,b,c,d){var ts=(t/=d)*t;var tc=ts*t;return b+c*(-3.4005*tc*ts+10.2*ts*ts+-6.2*tc+0.4*ts);};this.backOut=function(t,b,c,d){var ts=(t/=d)*t;var tc=ts*t;return b+c*(8.292*tc*ts+-21.88*ts*ts+22.08*tc+-12.69*ts+5.1975*t);};this.backBoth=function(t,b,c,d){var ts=(t/=d)*t;var tc=ts*t;return b+c*(0.402*tc*ts+-2.1525*ts*ts+-3.2*tc+8*ts+-2.05*t);};};YAHOO.util.Motion=function(el,attributes,duration,method){if(el){this.initMotion(el,attributes,duration,method);}};YAHOO.util.Motion.prototype=new YAHOO.util.Anim();YAHOO.util.Motion.prototype.defaultUnits.points='px';YAHOO.util.Motion.prototype.doMethod=function(attribute,start,end){var val=null;if(attribute=='points'){var translatedPoints=this.getTranslatedPoints();var t=this.method(this.currentFrame,0,100,this.totalFrames)/100;if(translatedPoints){val=YAHOO.util.Bezier.getPosition(translatedPoints,t);}}else{val=this.method(this.currentFrame,start,end-start,this.totalFrames);}return val;};YAHOO.util.Motion.prototype.getAttribute=function(attribute){var val=null;if(attribute=='points'){val=[this.getAttribute('left'),this.getAttribute('top')];if(isNaN(val[0])){val[0]=0;}if(isNaN(val[1])){val[1]=0;}}else{val=parseFloat(YAHOO.util.Dom.getStyle(this.getEl(),attribute));}return val;};YAHOO.util.Motion.prototype.setAttribute=function(attribute,val,unit){if(attribute=='points'){YAHOO.util.Dom.setStyle(this.getEl(),'left',val[0]+unit);YAHOO.util.Dom.setStyle(this.getEl(),'top',val[1]+unit);}else{YAHOO.util.Dom.setStyle(this.getEl(),attribute,val+unit);}};YAHOO.util.Motion.prototype.initMotion=function(el,attributes,duration,method){YAHOO.util.Anim.call(this,el,attributes,duration,method);attributes=attributes||{};attributes.points=attributes.points||{};attributes.points.control=attributes.points.control||[];this.attributes=attributes;var start;var end=null;var translatedPoints=null;this.getTranslatedPoints=function(){return translatedPoints;};var translateValues=function(val,self){var pageXY=YAHOO.util.Dom.getXY(self.getEl());val=[val[0]-pageXY[0]+start[0],val[1]-pageXY[1]+start[1]];return val;};var onStart=function(){start=this.getAttribute('points');var attributes=this.attributes;var control=attributes['points']['control']||[];if(control.length>0&&control[0].constructor!=Array){control=[control];}if(YAHOO.util.Dom.getStyle(this.getEl(),'position')=='static'){YAHOO.util.Dom.setStyle(this.getEl(),'position','relative');}if(typeof attributes['points']['from']!='undefined'){YAHOO.util.Dom.setXY(this.getEl(),attributes['points']['from']);start=this.getAttribute('points');}else if((start[0]===0||start[1]===0)){YAHOO.util.Dom.setXY(this.getEl(),YAHOO.util.Dom.getXY(this.getEl()));start=this.getAttribute('points');}var i,len;if(typeof attributes['points']['to']!='undefined'){end=translateValues(attributes['points']['to'],this);for(i=0,len=control.length;i<len;++i){control[i]=translateValues(control[i],this);}}else if(typeof attributes['points']['by']!='undefined'){end=[start[0]+attributes['points']['by'][0],start[1]+attributes['points']['by'][1]];for(i=0,len=control.length;i<len;++i){control[i]=[start[0]+control[i][0],start[1]+control[i][1]];}}if(end){translatedPoints=[start];if(control.length>0){translatedPoints=translatedPoints.concat(control);}translatedPoints[translatedPoints.length]=end;}};this._onStart.subscribe(onStart);};YAHOO.util.Scroll=function(el,attributes,duration,method){if(el){YAHOO.util.Anim.call(this,el,attributes,duration,method);}};YAHOO.util.Scroll.prototype=new YAHOO.util.Anim();YAHOO.util.Scroll.prototype.defaultUnits.scroll=' ';YAHOO.util.Scroll.prototype.doMethod=function(attribute,start,end){var val=null;if(attribute=='scroll'){val=[this.method(this.currentFrame,start[0],end[0]-start[0],this.totalFrames),this.method(this.currentFrame,start[1],end[1]-start[1],this.totalFrames)];}else{val=this.method(this.currentFrame,start,end-start,this.totalFrames);}return val;};YAHOO.util.Scroll.prototype.getAttribute=function(attribute){var val=null;var el=this.getEl();if(attribute=='scroll'){val=[el.scrollLeft,el.scrollTop];}else{val=parseFloat(YAHOO.util.Dom.getStyle(el,attribute));}return val;};YAHOO.util.Scroll.prototype.setAttribute=function(attribute,val,unit){var el=this.getEl();if(attribute=='scroll'){el.scrollLeft=val[0];el.scrollTop=val[1];}else{YAHOO.util.Dom.setStyle(el,attribute,val+unit);}};
