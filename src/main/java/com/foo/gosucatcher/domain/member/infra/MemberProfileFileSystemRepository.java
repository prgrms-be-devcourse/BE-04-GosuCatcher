package com.foo.gosucatcher.domain.member.infra;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.foo.gosucatcher.domain.member.domain.ImageFile;
import com.foo.gosucatcher.domain.member.domain.Member;
import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
import com.foo.gosucatcher.domain.member.exception.MemberFileIOException;
import com.foo.gosucatcher.global.error.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class MemberProfileFileSystemRepository implements MemberProfileRepository {

	private static final String PROFILE_IMAGE_NAME = "profile";
	private static final String DEFAULT_PROFILE = "default.png";

	@Value("${member.profile.root}")
	private String ROOT_DIRECTORY;

	@Override
	public void initializeMemberProfile(Member member) {
		Long memberId = member.getId();
		String profilePath = ROOT_DIRECTORY + "/" + memberId;
		createDirectory(profilePath);

		setDefaultProfileImage(member);
	}

	@Override
	public ImageFile uploadImage(Member member, MultipartFile file) {
		long memberId = member.getId();
		String contentType = Optional.ofNullable(file.getContentType())
			.orElseThrow(() -> new MemberFileIOException(ErrorCode.INTERNAL_SERVER_ERROR));
		String extension = contentType.split("/")[1];

		String fullPath = makeFullPath(memberId, contentType);
		writeImageOnFileSystem(file, fullPath);

		return ImageFile.builder()
			.path(fullPath)
			.fileName(PROFILE_IMAGE_NAME)
			.fileExtension(extension)
			.build();
	}

	@Override
	public ImageFile findImage(Member member) {
		if (member.getProfileImageFile() == null) {
			throw new MemberFileIOException(ErrorCode.NOT_FOUND_IMAGE);
		}

		return member.getProfileImageFile();
	}

	@Override
	public void deleteImage(Member member) {
		ImageFile imageFile = member.getProfileImageFile();

		String path = imageFile.getPath();
		if (path.equals(ROOT_DIRECTORY + DEFAULT_PROFILE)) {
			return;
		}

		File file = new File(path);
		try {
			file.delete();
		} catch (Exception e) {
			throw new MemberFileIOException(ErrorCode.NOT_FOUND_IMAGE);
		}

		setDefaultProfileImage(member);
	}

	private void createDirectory(String profilePath) {
		File folder = new File(profilePath);
		if (!folder.exists()) {
			boolean isCreated = folder.mkdir();
			if (!isCreated) {
				throw new MemberFileIOException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		}
	}

	private void setDefaultProfileImage(Member member) {
		String defaultProfilePath = ROOT_DIRECTORY + "/" + DEFAULT_PROFILE;

		String[] defaultProfileInfo = DEFAULT_PROFILE.split("\\.");
		String defaultFileName = defaultProfileInfo[0];
		String defaultFileExtension = defaultProfileInfo[1];

		ImageFile defaultImageFile = ImageFile.builder()
			.path(defaultProfilePath)
			.fileName(defaultFileName)
			.fileExtension(defaultFileExtension)
			.build();

		member.changeProfileImageFile(defaultImageFile);
	}

	private String makeFullPath(long memberId, String contentType) {
		String subType = contentType.split("/")[1];

		return ROOT_DIRECTORY + "/" + memberId
			+ "/" + PROFILE_IMAGE_NAME + "." + subType;
	}

	private void writeImageOnFileSystem(MultipartFile file, String fullPath) {
		log.warn(fullPath);
		try {
			file.transferTo(new File(fullPath));
		} catch (IOException e) {
			throw new MemberFileIOException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
