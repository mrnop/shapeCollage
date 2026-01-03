package com.isarainc.stickers;

import com.isarainc.fonts.Font;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

@Entity(
		indexes = {@Index(value = "infoName,folder", unique = true)}
)
public class StickerInfo implements Comparable<Font>{
	public transient static final String TYPE_BUNDLE="bundle";
	public transient static final String TYPE_DOWNLOAD="download";
	public transient static final String TYPE_LINE="line";
	public transient static final String TYPE_LINE_CAMERA_SECTION="linecamera_sec";
	public transient static final String TYPE_LINE_CAMERA_STAMP="linecamera_stamp";
	public transient static final String TYPE_MOMENTCAM="momentcam";
	public transient static final String TYPE_BGREMOVER="bgremover";
	public transient static final String TYPE_CHATON="chaton";
	
	@Id
	private Long id;
	private String infoName;
	private String folder;
	private String path;
	private String type=TYPE_BUNDLE;
	private Date created;

	@Generated(hash = 1051714146)
	public StickerInfo() {
	}

	@Generated(hash = 1075551738)
	public StickerInfo(Long id, String infoName, String folder, String path,
			String type, Date created) {
		this.id = id;
		this.infoName = infoName;
		this.folder = folder;
		this.path = path;
		this.type = type;
		this.created = created;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInfoName() {
		return infoName;
	}

	public void setInfoName(String name) {
		this.infoName = name;
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
			object.put("name", infoName);
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

	public static StickerInfo fromJson(String json) {
		StickerInfo sticker = new StickerInfo();

		JSONObject jsonObj;

		try {
			jsonObj = new JSONObject(json);
			
			try {
				sticker.setInfoName(jsonObj.getString("name"));
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
		return "StickerInfo{" +
				"id=" + id +
				", infoName='" + infoName + '\'' +
				", folder='" + folder + '\'' +
				", path='" + path + '\'' +
				", type='" + type + '\'' +
				", created=" + created +
				'}';
	}

	@Override
	public int compareTo(Font arg0) {
		return infoName.compareTo(arg0.getName());
	}

	
	
	

}
