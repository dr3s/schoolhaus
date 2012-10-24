package models

import play.api.Play.current
import db.amzn._
import com.amazonaws.services.dynamodb.model._
import scala.collection.Map
import scalaj.collection.Imports._
import java.util.UUID


case class User(id: Option[String] = None, email: String, name: String, openId: Option[String])

object User {
  
  // -- Parsers
  
  /**
   * Parse a User from a ResultSet
   
  val simple = {
    get[String]("user.email") ~
    get[String]("user.name") map {
      case email~name => User(email, name)
    }
    }
    */
    def fromMap(m: Map[String,AttributeValue]): User =  {
    	val id = m.get("user-id") map {_.getS}
    	val email = m.get("email").get.getS
    	val name = m.get("name").get.getS
    	val openId = m.get("openId") map {_.getS}
    	User(id, email, name, openId)
    }
  
  
  
  // -- Queries
  
  /**
   * Retrieve a User from email.
   */
  def findByEmail(email: String): Option[User] = {
    None
  }
  
  /**
   * Retrieve a User from open id.
   */
  def findByOpenId(openId: String): User = {
    val result = DynamoDb.getItem { request =>
      request.setTableName("sh-openid")
      request.withKey( new Key().withHashKeyElement(new AttributeValue().withS(openId)))
    }
    val userAttr = result.getItem().asScala
    fromMap (userAttr)
    
  }
  
  
  
  /**
   * Authenticate a User.
   */
  def authenticate(email: String, password: String): Option[User] = {
    None
  }
   
  /**
   * Create a User.
   */
  def create(user: User): User = {
    val id = UUID.randomUUID.toString
    val result = DynamoDb.putItem { request =>
      request.setTableName("sh-users")
      request.withItem(Map[String,AttributeValue]( 
          "user-id" -> new AttributeValue().withS(id),
          "email" -> new AttributeValue().withS(user.email)) 
          	asJava)
    }
     
    user.copy(id = Some(id))
  }
  
}
