SUMMARY = "Open Package Manager"
SUMMARY_libopkg = "Open Package Manager library"
SECTION = "base"
HOMEPAGE = "http://code.google.com/p/opkg/"
BUGTRACKER = "http://code.google.com/p/opkg/issues/list"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
					file://src/opkg.c;beginline=2;endline=21;md5=90435a519c6ea69ef22e4a88bcc52fa0"

DEPENDS = "libarchive"

inherit autotools pkgconfig systemd gitpkgv

PE = "1"

PV = "0.4.0+git${SRCPV}"
PKGV = "0.4.0+git${GITPKGV}"

SRC_URI = " git://git.yoctoproject.org/opkg \
			file://01-opkg_conf-create-opkg.lock-in-run-instead-of-var-run.patch \
			file://02-revert-commit-7a8c2f6.patch \
			file://opkg-configure.service \
			file://opkg.conf \
			file://modprobe \
			file://opkg-wget \
			"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} = "opkg-configure.service"

target_localstatedir := "${localstatedir}"
OPKGLIBDIR = "${target_localstatedir}/lib"

PACKAGECONFIG ??= ""

PACKAGECONFIG[gpg] 			= "--enable-gpg,--disable-gpg,gpgme libgpg-error,gnupg"
PACKAGECONFIG[curl] 		= "--enable-curl,--disable-curl,curl"
PACKAGECONFIG[ssl-curl] 	= "--enable-ssl-curl,--disable-ssl-curl,curl openssl"
PACKAGECONFIG[openssl] 		= "--enable-openssl,--disable-openssl,openssl"
PACKAGECONFIG[sha256] 		= "--enable-sha256,--disable-sha256"
PACKAGECONFIG[pathfinder] 	= "--enable-pathfinder,--disable-pathfinder,pathfinder"
PACKAGECONFIG[libsolv] 		= "--with-libsolv,--without-libsolv,libsolv"

EXTRA_OECONF_class-native = "--localstatedir=/${@os.path.relpath('${localstatedir}', '${STAGING_DIR_NATIVE}')} --sysconfdir=/${@os.path.relpath('${sysconfdir}', '${STAGING_DIR_NATIVE}')}"

do_install_prepend() {
	install -d ${D}${datadir}/opkg/intercept
	install -m 755 ${WORKDIR}/modprobe ${D}${datadir}/opkg/intercept/
	install -d ${D}${bindir}
	install -m 755 ${WORKDIR}/opkg-wget ${D}${bindir}/
}

do_install_append () {
	install -d ${D}${sysconfdir}/opkg
	install -m 0644 ${WORKDIR}/opkg.conf ${D}${sysconfdir}/opkg/opkg.conf
	echo "option lists_dir ${OPKGLIBDIR}/opkg/lists" >>${D}${sysconfdir}/opkg/opkg.conf

	# We need to create the lock directory
	install -d ${D}${OPKGLIBDIR}/opkg

	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)};then
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/opkg-configure.service ${D}${systemd_unitdir}/system/
		sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
			-e 's,@SYSCONFDIR@,${sysconfdir},g' \
			-e 's,@BINDIR@,${bindir},g' \
			-e 's,@SYSTEMD_UNITDIR@,${systemd_unitdir},g' \
			${D}${systemd_unitdir}/system/opkg-configure.service
	fi
}

RDEPENDS_${PN} = "${VIRTUAL-RUNTIME_update-alternatives} opkg-arch-config run-postinsts libarchive"
RDEPENDS_${PN}_class-native = ""
RDEPENDS_${PN}_class-nativesdk = ""
RDEPENDS_libopkg += "opkg-wget"
RREPLACES_${PN} = "opkg-nogpg opkg-collateral"
RCONFLICTS_${PN} = "opkg-collateral"
RPROVIDES_${PN} = "opkg-collateral"

PACKAGES =+ "libopkg opkg-wget"

FILES_libopkg = "${libdir}/*.so.* ${OPKGLIBDIR}/opkg/"
FILES_opkg-wget = "${bindir}/opkg-wget"
FILES_${PN} += "${systemd_unitdir}/system/"

BBCLASSEXTEND = "native nativesdk"

CONFFILES_${PN} = "${sysconfdir}/opkg/opkg.conf"
