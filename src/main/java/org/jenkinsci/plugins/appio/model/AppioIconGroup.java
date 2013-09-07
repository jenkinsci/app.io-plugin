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

/**
 * User: Mark Prichard (mprichard@cloudbees.com))
 * Date: 9/6/13
 */
public class AppioIconGroup {
    String url;
    AppioIcon small;
    AppioIcon iphone2x;
    AppioIcon iphone;
    AppioIcon ipad2x;
    AppioIcon ipad;
    AppioIcon facebook;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AppioIcon getSmall() {
        return small;
    }

    public void setSmall(AppioIcon small) {
        this.small = small;
    }

    public AppioIcon getIphone2x() {
        return iphone2x;
    }

    public void setIphone2x(AppioIcon iphone2x) {
        this.iphone2x = iphone2x;
    }

    public AppioIcon getIphone() {
        return iphone;
    }

    public void setIphone(AppioIcon iphone) {
        this.iphone = iphone;
    }

    public AppioIcon getIpad2x() {
        return ipad2x;
    }

    public void setIpad2x(AppioIcon ipad2x) {
        this.ipad2x = ipad2x;
    }

    public AppioIcon getIpad() {
        return ipad;
    }

    public void setIpad(AppioIcon ipad) {
        this.ipad = ipad;
    }

    public AppioIcon getFacebook() {
        return facebook;
    }

    public void setFacebook(AppioIcon facebook) {
        this.facebook = facebook;
    }
}
