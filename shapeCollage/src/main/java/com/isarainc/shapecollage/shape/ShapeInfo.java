package com.isarainc.shapecollage.shape;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@Entity(
		indexes = {@Index(value = "info,folder", unique = true)}
)
public class ShapeInfo{
	public transient static final String TYPE_BUNDLE="bundle";
	public transient static final String TYPE_CUSTOM="custom";


	@Id
	private Long id;
	private String info;
	private String folder;
	private String path;
	private String type=TYPE_BUNDLE;
	private Date created;



	@Generated(hash = 784858565)
	public ShapeInfo(Long id, String info, String folder, String path, String type,
			Date created) {
		this.id = id;
		this.info = info;
		this.folder = folder;
		this.path = path;
		this.type = type;
		this.created = created;
	}

	@Generated(hash = 603002925)
	public ShapeInfo() {
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String name) {
		this.info = name;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	public JSONObject toJsonObject() {
		JSONObject object = new JSONObject();

		try {
			object.put("name", info);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			object.put("path", path);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			object.put("folder", folder);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			object.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object;
	}
	
	public String toJson() {
		return toJsonObject().toString();
	}

	public static ShapeInfo fromJson(String json) {
		ShapeInfo sticker = new ShapeInfo();

		JSONObject jsonObj;

		try {
			jsonObj = new JSONObject(json);
			
			try {
				sticker.setInfo(jsonObj.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				sticker.setPath(jsonObj.getString("path"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				sticker.setFolder(jsonObj.getString("folder"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				sticker.setType(jsonObj.getString("type"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			

			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		return sticker;
	}

	@Override
	public String toString() {
		return "StickerInfo [name=" + info + ", path=" + path + ", type="
				+ type + "]";
	}

	
	
	

}
