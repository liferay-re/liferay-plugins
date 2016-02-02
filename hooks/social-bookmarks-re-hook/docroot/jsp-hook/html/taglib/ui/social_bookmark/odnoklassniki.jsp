<%@ include file="/html/taglib/ui/social_bookmark/init.jsp" %>

<%
String okContentId = GetterUtil.getString((String)request.getAttribute("liferay-ui:social-bookmark:contentId"));
%>

<div id="ok_like_<%= okContentId %>"></div>
<script>
!function (d, id, did, st) {
  var js = d.createElement("script");
  js.src = "https://connect.ok.ru/connect.js";
  js.onload = js.onreadystatechange = function () {
  if (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") {
    if (!this.executed) {
      this.executed = true;
      setTimeout(function () {
        OK.CONNECT.insertShareWidget(id,did,st);
      }, 0);
    }
  }};
  d.documentElement.appendChild(js);
}(document,"ok_like_<%= okContentId %>",document.URL,"{width:125,height:25,st:'oval',sz:12,ck:1}");
</script>
