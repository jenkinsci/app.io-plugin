/*
 * The MIT License
 *
 * Copyright (c) 2013 Mark Prichard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.appio.model;

import com.google.gson.annotations.Expose;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model class for App.io REST API v1
 * 
 * @see <a href="http://docs.app.io/api">http://docs.app.io/api</a>
 * @author Mark Prichard
 */
@XmlRootElement
public class AppioAppObject {
	private String id = null;
	private String user_id = null;
	@Expose
	private String name = null;
	private String public_key = null;
	private String bundle = null;
	//private String icon = null;
	//private AppioIconGroup icon;

    private boolean default_to_landscape = false;
	private String device_type = null;
	private String description = null;
	private Double price;
	private String formatted_price;
	private String developer_name;
	private String release_date;
	private String created_at;
	private String updated_at;
	private String[] version_ids;


	public AppioAppObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublic_key() {
		return public_key;
	}

	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

    /*
    public AppioIconGroup getIcon() {
        return icon;
    }

    public void setIcon(AppioIconGroup icon) {
        this.icon = icon;
    }
    */

    public boolean isDefault_to_landscape() {
		return default_to_landscape;
	}

	public void setDefault_to_landscape(boolean default_to_landscape) {
		this.default_to_landscape = default_to_landscape;
	}

	public String getDevice_type() {
		return device_type;
	}

	public void setDevice_type(String device_type) {
		this.device_type = device_type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getFormatted_price() {
		return formatted_price;
	}

	public void setFormatted_price(String formatted_price) {
		this.formatted_price = formatted_price;
	}

	public String getDeveloper_name() {
		return developer_name;
	}

	public void setDeveloper_name(String developer_name) {
		this.developer_name = developer_name;
	}

	public String getRelease_date() {
		return release_date;
	}

	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String[] getVersion_ids() {
		return version_ids;
	}

	public void setVersion_ids(String[] version_ids) {
		this.version_ids = version_ids;
	}
}
