package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import views._
import play.api.libs.openid._
import play.api.libs.concurrent._

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
  
  def loginWithOpenId = Action { implicit request =>
	  Form(single(
	    "openid" -> nonEmptyText
	  )).bindFromRequest.fold(
	    error => {
	      Logger.info("bad request " + error.toString)
	      BadRequest(error.toString)
	    },
	    {
	      case (openid) => AsyncResult(OpenID.redirectURL(
				    openid,
				    routes.Application.openIDCallback.absoluteURL(),
				    Seq("email" -> "http://schema.openid.net/contact/email")
				)
	          .extend( _.value match {
	              case Redeemed(url) => Redirect(url)
	              case Thrown(t) => Redirect(routes.Application.login)
	          }))
	    }
	  )
	}
	
	def openIDCallback = Action { implicit request =>
	  AsyncResult(
	    OpenID.verifiedId.extend( _.value match {
	      case Redeemed(info) => {
	    	User.create(User(None, info.attributes("email"), "some name", Some(info.id)))  
	        Logger.info("created user " + info.attributes("email"))
	        Redirect( routes.Application.index())
		      .withSession(
				  session + ("email" -> info.attributes("email"))
				)

	      }
	      case Thrown(t) => {
	        // Here you should look at the error, and give feedback to the user
	    	  Logger.error("bad request ", t)
	        Redirect(routes.Application.login)
	      }
	    })
	  )
	}


  
  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.login(formWithErrors)),
      user => Redirect(routes.Application.index).withSession("email" -> user._1)
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