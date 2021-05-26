package it.cs.contact.tracing.be.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.cs.contact.tracing.be.utils.Util;
import lombok.*;

@DynamoDBTable(tableName = "second-level-contacts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Contact {

	@DynamoDBHashKey(attributeName = "device-key")
	@JsonProperty
	private String deviceKey;

	@DynamoDBAttribute(attributeName = "communicated-on")
	@JsonProperty
	private Integer communicatedOn;

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Contact{");
		sb.append("devicekey='").append(deviceKey).append('\'');
		sb.append(", communicatedOn=").append(communicatedOn);
		sb.append('}');
		return sb.toString();
	}

	@DynamoDBIgnore
	public boolean isValid() {

		return !StringUtils.isNullOrEmpty(deviceKey) && Util.isValidDate(communicatedOn);
	}
}