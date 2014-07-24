package herokuSamples.web.auth;

import java.util.Map;
import java.util.List;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class UserInfo {

	public static UserInfo fromJson(String str) {
		Type type = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, Object> map = new Gson().fromJson(str, type);
		return new UserInfo(map);
	}
	private Map<String, Object> map;

	public UserInfo(Map<String, Object> map) {
		this.map = map;
	}

	public Object getValue(String name) { return this.map.get(name);}
	public String getValueAsString(String name) { 
		Object ret = this.map.get(name);
		return ret == null ? "" : ret.toString();
	}

	public String getUserId() { return getValueAsString("user_id");}
	public String getName() { return getValueAsString("name");}
	public String getEmail() { return getValueAsString("email");}
	public String getPictureUrl() { return getValueAsString("picture");}
	public String getGivenName() { return getValueAsString("given_name");}
	public String getFamilyName() { return getValueAsString("family_name");}

	public Map<String, Object> getIdentityInfo() {
		Object o = this.map.get("identities");
		if (o == null) {
			return null;
		} else if (o instanceof Map) {
			return (Map<String, Object>)o;
		} else if (o instanceof List) {
			List list = (List)o;
			return list.size() == 0 ? null : (Map<String, Object>)list.get(0);
		} else {
			throw new IllegalStateException(o.toString());
		}

	}

	public String getConnection() { 
		Map<String, Object> identity = getIdentityInfo();
		if (identity == null) {
			return "";
		}
		String ret = (String)identity.get("connection");
		return ret == null ? "" : ret;
	}
}