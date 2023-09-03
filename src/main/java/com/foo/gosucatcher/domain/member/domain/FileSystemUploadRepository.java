package com.foo.gosucatcher.domain.member.domain;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class FileSystemUploadRepository {

	@Value("${member.profile.root}")
	private String rootDirectory;

	public String uploadProfileImage(long memberId, MultipartFile file) {
		String memberProfilePath = rootDirectory + "/" + memberId;
		File folder = new File(memberProfilePath);
		if (!folder.exists()) {
			try {
				folder.mkdir();
			} catch (Exception e) {
				throw new RuntimeException("폴더 못만듦 ㅅㄱ");
			}
		}

		String fullPath = memberProfilePath + "/profile." + file.getContentType().split("/")[1];
		try {
			file.transferTo(new File(fullPath));
		} catch (IOException e) {
			throw new RuntimeException("파일저장 실패");
		}

		return fullPath;
	}

	public Resource findProfileImage(Member member) {
		String profileImagePath = member.getProfileImagePath();
		try {
			return new UrlResource("file:" + profileImagePath);
		} catch (MalformedURLException e) {
			throw new RuntimeException("잘못된 url임");
		}
	}

	public String getProfileRootDirectory() {
		return this.rootDirectory + "/default.png";
	}

	public void deleteProfileImage(Member member) {
		String profileImagePath = member.getProfileImagePath();
		if (profileImagePath.equals(rootDirectory + "/default.png")) {
			return;
		}
		File file = new File(profileImagePath);
		file.delete();
	}
}
