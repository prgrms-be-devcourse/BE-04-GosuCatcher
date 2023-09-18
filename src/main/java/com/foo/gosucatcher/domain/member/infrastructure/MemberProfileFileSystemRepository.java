// package com.foo.gosucatcher.domain.member.infrastructure;
//
// import java.io.File;
// import java.io.IOException;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Repository;
// import org.springframework.util.StringUtils;
// import org.springframework.web.multipart.MultipartFile;
//
// import com.foo.gosucatcher.domain.member.domain.Member;
// import com.foo.gosucatcher.domain.member.domain.MemberImage;
// import com.foo.gosucatcher.domain.member.domain.MemberProfileRepository;
// import com.foo.gosucatcher.domain.member.exception.MemberFileIOException;
// import com.foo.gosucatcher.global.error.ErrorCode;
//
// @Repository
// public class MemberProfileFileSystemRepository implements MemberProfileRepository {
//
// 	private static final String PROFILE_IMAGE_NAME = "profile";
//
// 	@Value("${member.profile.root}")
// 	private String ROOT_DIRECTORY;
//
// 	@Override
// 	public void initializeMemberProfile(Member member) {
// 		Long memberId = member.getId();
// 		String profilePath = ROOT_DIRECTORY + "/" + memberId;
// 		createDirectory(profilePath);
//
// 		MemberImage defaultMemberImage = createDefaultImageFile();
// 		defaultMemberImage.changePathToDefault();
// 		member.updateProfileImage(defaultMemberImage);
// 	}
//
// 	private MemberImage createDefaultImageFile() {
// 		return MemberImage.builder()
// 			.fileName("default")
// 			.fileExtension("png")
// 			.build();
// 	}
//
// 	@Override
// 	public MemberImage uploadImage(Member member, MultipartFile file) {
// 		long memberId = member.getId();
// 		String extension = getExtension(file);
//
// 		String fullPath = makeFullPath(memberId, extension);
// 		writeImageOnFileSystem(file, fullPath);
//
// 		return MemberImage.builder()
// 			.path(fullPath)
// 			.fileName(PROFILE_IMAGE_NAME)
// 			.fileExtension(extension)
// 			.build();
// 	}
//
// 	@Override
// 	public MemberImage findImage(Member member) {
// 		if (member.getProfileMemberImage() == null) {
// 			throw new MemberFileIOException(ErrorCode.NOT_FOUND_IMAGE);
// 		}
//
// 		return member.getProfileMemberImage();
// 	}
//
// 	@Override
// 	public void deleteImage(MemberImage image) {
// 		if (image.isDefaultImagePath()) {
// 			return;
// 		}
//
// 		String path = image.getPath();
// 		File file = new File(path);
// 		try {
// 			file.delete();
// 		} catch (Exception e) {
// 			throw new MemberFileIOException(ErrorCode.NOT_FOUND_IMAGE);
// 		}
//
// 		image.changePathToDefault();
// 	}
//
// 	private void createDirectory(String profilePath) {
// 		File folder = new File(profilePath);
// 		if (folder.exists()) {
// 			return;
// 		}
//
// 		boolean isCreated = folder.mkdir();
// 		if (!isCreated) {
// 			throw new MemberFileIOException(ErrorCode.INTERNAL_SERVER_ERROR);
// 		}
// 	}
//
// 	private String makeFullPath(long memberId, String extension) {
// 		return ROOT_DIRECTORY + "/" + memberId
// 			+ "/" + PROFILE_IMAGE_NAME + "." + extension;
// 	}
//
// 	private void writeImageOnFileSystem(MultipartFile file, String fullPath) {
// 		try {
// 			file.transferTo(new File(fullPath));
// 		} catch (IOException e) {
// 			throw new MemberFileIOException(ErrorCode.INTERNAL_SERVER_ERROR);
// 		}
// 	}
//
// 	private String getExtension(MultipartFile multipartFile) {
// 		return StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());
// 	}
// }
