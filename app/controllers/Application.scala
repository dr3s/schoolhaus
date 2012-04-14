package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import views._
import play.api.libs.openid.OpenID

object Application extends Controller with Secured {
   
  def index = IsAuthenticated { username => _ =>
    Ok( 
        html.index(username)
      )
  }
  
  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => true
        
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(html.login(loginForm))
  }
  
  /**
   * OpenId VerifyId.
   */
  def verifyid = Action { implicit request =>
    AsyncResult {
       OpenID.verifiedId(request).map {
         
         userInfo => Ok( html.index(userInfo.attributes.getOrElse("email", "unknown")))
       }
      }
    
               
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Async {
        		OpenID.redirectURL(user._1, routes.Application.verifyid.absoluteURL(), List("email" -> "http://schema.openid.net/contact/email") ).map(i => Redirect( i, 302) )
        }
    )
    
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  

  
}