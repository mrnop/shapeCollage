package com.isarainc.stickers;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

@Entity
public class Sticker implements Comparable<Sticker> {

	@Id
	private Long id;
	private String refNo;
	private String stickerName;
	private String remoteUrl;
	private String file;
	private Boolean active;
	private Date created;


	@Generated(hash = 1962319185)
	public Sticker(Long id, String refNo, String stickerName, String remoteUrl,
			String file, Boolean active, Date created) {
		this.id = id;
		this.refNo = refNo;
		this.stickerName = stickerName;
		this.remoteUrl = remoteUrl;
		this.file = file;
		this.active = active;
		this.created = created;
	}

	@Generated(hash = 1542104920)
	public Sticker() {
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String ref) {
		this.refNo = ref;
	}

	public String getStickerName() {
		return stickerName;
	}

	public void setStickerName(String name) {
		this.stickerName = name;
	}

	


	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	
	public boolean isFileExists() {
	
		if (file == null)
			return false;
		File f = new File(file);
		return f.exists();
	}


	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}



	public String toJson() {
		JSONObject object = new JSONObject();

		try {
			object.put("id", id);
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		try {
			object.put("name", stickerName);
		} catch (JSONException e) {
			//e.printStackTrace();
		}
		try {
			object.put("ref", refNo);
		} catch (JSONException e) {
			//e.printStackTrace();
		}

		try {
			object.put("remoteUrl", remoteUrl);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			object.put("file", file);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			object.put("active", active);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return object.toString();
	}

	public static Sticker fromJson(String json) {
		Sticker sticker = new Sticker();

		JSONObject jsonObj;

		try {
			jsonObj = new JSONObject(json);
			try {
				sticker.setId(jsonObj.getLong("id"));
			} catch (JSONException e) {
				//e.printStackTrace();
			}
			try {
				sticker.setStickerName(jsonObj.getString("name"));
			} catch (JSONException e) {
				//e.printStackTrace();
			}
			try {
				sticker.setRefNo(jsonObj.getString("ref"));
			} catch (JSONException e) {
				//e.printStackTrace();
			}
			try {
				sticker.setRemoteUrl(jsonObj.getString("remoteUrl"));
			} catch (JSONException e) {
				//e.printStackTrace();
			}
			try {
				sticker.setFile(jsonObj.getString("file"));
			} catch (JSONException e) {
				//e.printStackTrace();
			}

			try {
				sticker.setActive(jsonObj.getBoolean("active"));
			} catch (JSONException e) {
				//e.printStackTrace();
			}

			
		} catch (JSONException e1) {

			//e1.printStackTrace();
		}

		return sticker;
	}

	@Override
	public int compareTo(Sticker another) {
		return stickerName.compareTo(another.getStickerName());
	}

	@Override
	public String toString() {
		return "Font [id=" + id + ", ref=" + refNo + ", name=" + stickerName
				+ ", remoteUrl=" + remoteUrl + ", file=" + file
				 + ", created=" + created + "]";
	}

	public boolean getActive() {
		return this.active;
	}

	

}
