package db.amzn

import com.amazonaws.auth._
import play.api.Play.current
import com.amazonaws.services.dynamodb._
import com.amazonaws.services.dynamodb.model._
import scalaj.collection.Imports._
import scala.util.parsing.combinator.Parser

object DynamoDb {

  val client = {
		val keyId = current.configuration.getString("amzn.key.id").get
		val keySecret = current.configuration.getString("amzn.key.secret").get
        val credentials = new BasicAWSCredentials(keyId, keySecret)

        new AmazonDynamoDBClient(credentials);
    }
  
  /**
   * Put
   */
  def putItem[T](f: PutItemRequest => T) : PutItemResult = { 
    val req:PutItemRequest = new PutItemRequest()
    
    f(req)
    client.putItem(req)
  
  }
  
   /**
   * Get
   */
  def getItem[T](f: GetItemRequest => T) : GetItemResult = { 
    val req:GetItemRequest = new GetItemRequest()
    
    f(req)
    client.getItem(req)
  
  }
  
  
  
}

class MapParser extends Parser[Map[String, AttributeValue]]