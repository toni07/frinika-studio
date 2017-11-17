/*
 * Created on Sep 14, 2007
 *
 * Copyright (c) 2004-2007 Peter Johan Salomonsen
 * 
 * http://www.frinika.com
 * 
 * This file is part of Frinika.
 * 
 * Frinika is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * Frinika is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with Frinika; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.frinika.release;

import static com.frinika.project.dialog.VersionProperties.getBuildDate;
import static com.frinika.project.dialog.VersionProperties.getVersion;
import java.io.File;

/**
 * Automatically rename released file to include version information
 * @author Peter Johan Salomonsen
 *
 */
public class ApplyReleaseVersion {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File("../frinika.zip");
		file.renameTo(new File("../frinika-"+getVersion()+"-"+getBuildDate()+".zip"));
	}

}