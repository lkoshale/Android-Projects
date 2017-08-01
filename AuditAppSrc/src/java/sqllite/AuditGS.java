package com.machadalo.audit.sqllite;

/*GETTER AND SETTERS*/

public class AuditGS {

	// private variables

	String _ad_inventory_ID;
	String _society_address;
	String _inventory_address;
	String _map;
	String _image;
	String _date;
	String _submit_status;
	String _timestamp;
	String _society_name;
	String _invent_type;

	String _ad_type;


	// Empty constructor
	public AuditGS() {

	}

	// constructor
	/*public AuditGS( String adID, String society_address,String inventory_address,String map
	,String image,String date,String submit_status,String timestamp) {

		this._ad_inventory_ID = adID;
		this._society_address = society_address;
		this._inventory_address = inventory_address;
		this._map = map;
		this._image = image;
		this._date = date;
		this._submit_status = submit_status;
		this._timestamp = timestamp;
	}*/
	public AuditGS(String adID, String society_address, String inventory_address, String map
			, String image, String date, String submit_status, String timestamp) {
		this._ad_inventory_ID = adID;
		this._society_address = society_address;
		this._inventory_address = inventory_address;
		this._map = map;
		this._image = image;
		this._date = date;
		this._submit_status = submit_status;
		this._timestamp = timestamp;

	}
	public AuditGS(String adID, String society_address, String inventory_address, String map
			, String image, String date, String submit_status, String timestamp,String invent_type,String society_name,String adType) {
		this._ad_inventory_ID = adID;
		this._society_address = society_address;
		this._inventory_address = inventory_address;
		this._map = map;
		this._image = image;
		this._date = date;
		this._submit_status = submit_status;
		this._timestamp = timestamp;
		this._invent_type = invent_type;
		this._society_name = society_name;
		this._ad_type = adType;

	}


	public AuditGS(String adID, String society_address, String inventory_address, String image, String submit_status, String date) {
		this._ad_inventory_ID = adID;
		this._society_address = society_address;
		this._inventory_address = inventory_address;
		this._image = image;
		this._submit_status = submit_status;
		this._date = date;
	}

	/*public AuditGS(int keyId) {
		this._id = keyId;

	}*/
	public AuditGS(String image) {
		this._image = image;

	}


	public String get_ad_inventory_ID() {
		return _ad_inventory_ID;
	}

	public void set_ad_inventory_ID(String _ad_inventory_ID) {
		this._ad_inventory_ID = _ad_inventory_ID;
	}

	public String get_society_address() {
		return _society_address;
	}

	public void set_society_address(String _society_address) {
		this._society_address = _society_address;
	}

	public String get_inventory_address() {
		return _inventory_address;
	}

	public void set_inventory_address(String _inventory_address) {
		this._inventory_address = _inventory_address;
	}

	public String get_map() {
		return _map;
	}

	public void set_map(String _map) {
		this._map = _map;
	}

	public String get_image() {
		return _image;
	}

	public void set_image(String _image) {
		this._image = _image;
	}

	public String get_date() {
		return _date;
	}

	public void set_date(String _date) {
		this._date = _date;
	}

	public String get_submit_status() {
		return _submit_status;
	}

	public void set_submit_status(String _submit_status) {
		this._submit_status = _submit_status;
	}

	public String get_timestamp() {
		return _timestamp;
	}

	public void set_timestamp(String _timestamp) {
		this._timestamp = _timestamp;
	}

	public String get_society_name() {
		return _society_name;
	}

	public void set_society_name(String _society_name) {
		this._society_name = _society_name;
	}
	public String get_invent_type() {
		return _invent_type;
	}

	public void set_invent_type(String _invent_type) {
		this._invent_type = _invent_type;
	}

	public String get_ad_type() {
		return _ad_type;
	}

	public void set_ad_type(String _ad_type) {
		this._ad_type = _ad_type;
	}

}