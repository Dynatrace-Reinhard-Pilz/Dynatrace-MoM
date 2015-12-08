<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%><!DOCTYPE html>
<html lang="en">
	<head>
        <title>Login page</title>
		<link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" type="image/x-icon" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/lib/jquery-ui-1.9.0.custom.min.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/icomoon.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/header.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/widgets.css" />

<!--[if IE]>
	<style type="text/css">
		.event_log_header .incidents .circle {
			background: #ff2626 !important;
			filter: none !important;
		}

		.event_log_header .incidents .circle:hover {
			background: #ff4e4e !important;
			filter: none !important;
		}	

		.event_log_header .incidents .circle.selected {
			background: #b50000 !important;
			filter: none !important;			
		}

		.event_log_header .incidents .circle.disabled {
			background: #878D91 !important;
			filter: none !important;
		}	
	</style>
<![endif]-->
        <script src="${pageContext.request.contextPath}/scripts/jquery-1.9.0.js"></script>
        <script type="text/javascript">
        	window.beforeUnload = false;
        	$(function() {
        		$(window).bind('beforeunload', function (e) {
        			window.beforeUnload = true;
        		});
        	});
        	</script>
        	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/login_page.css" />
        <script src="${pageContext.request.contextPath}/scripts/login.js"></script>
    </head>
    <body>
        <div class="login_content">
            <div class="login_form">
                <div class="logo">
                    <img src="${pageContext.request.contextPath}/images/Dynatrace_logo_big.png" alt="logo" />
                </div>                
                <div class="status">
                	<div id="browserNotSupportedMessage">
                		We are sorry but your browser is not supported. Please try Google Chrome, Mozilla Firefox, Safari or Internet Explorer 9+.
                	</div>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/login">
                    <div class="login_form_header">MoM</div>
                    <div class="login_form_content">
                        <div class="login_form_element">
                        	<input type="text" name="login" placeholder="Username" class="form_input" />
                        </div>
                        <div class="login_form_element">
                        	<input type="password" name="password" placeholder="Password" class="form_input" />
                        </div>
                    </div>
                    <div class="login_form_submit">
                        <input class="btn btn-green" type="submit" value="Sign in" />
                    </div>
                </form>
                <div class="footer">
                	<span>
                		Version:
                		1.0.717
                	</span><%--
                	<a class="link" href="https://community.dynatrace.com/community/pages/viewpage.action?pageId=195461209" target="_blank" title="Orchestration Platform Release Notes">
						Release Notes
						<i class="icon-external_link"></i>
					</a>
					<a class="link" href="https://apmcommunity.compuware.com/community/display/APMAASDOC/" target="_blank" title="Dynatrace documentation">
						Documentation
						<i class="icon-external_link"></i>
					</a>--%>
                </div>
            </div>
        </div>
    </body>
</html>