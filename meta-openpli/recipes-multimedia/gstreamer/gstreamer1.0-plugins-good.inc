require gstreamer1.0-plugins.inc

LICENSE = "GPLv2+ & LGPLv2.1+"

DEPENDS += "gstreamer1.0-plugins-base libcap zlib bzip2"

inherit gettext

# gtk is not in the PACKAGECONFIG variable by default until
# the transition to gtk+3 is finished
PACKAGECONFIG ??= " \
	${GSTREAMER_ORC} \
	${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} \
	cairo flac gdk-pixbuf gudev jpeg lame libpng mpg123 soup speex taglib v4l2 wavpack vpx \
	"

X11DEPENDS = "virtual/libx11 libsm libxrender libxfixes libxdamage"

PACKAGECONFIG[cairo]		= "--enable-cairo,--disable-cairo,cairo"
PACKAGECONFIG[dv1394]		= "--enable-dv1394,--disable-dv1394,libiec61883 libavc1394 libraw1394"
PACKAGECONFIG[flac]			= "--enable-flac,--disable-flac,flac"
PACKAGECONFIG[gdk-pixbuf]	= "--enable-gdk_pixbuf,--disable-gdk_pixbuf,gdk-pixbuf"
PACKAGECONFIG[gtk]			= "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[gudev]		= "--with-gudev,--without-gudev,libgudev"
PACKAGECONFIG[jack]			= "--enable-jack,--disable-jack,jack"
PACKAGECONFIG[jpeg]			= "--enable-jpeg,--disable-jpeg,jpeg"
PACKAGECONFIG[lame]			= "--enable-lame,--disable-lame,lame"
PACKAGECONFIG[libpng]		= "--enable-libpng,--disable-libpng,libpng"
PACKAGECONFIG[libv4l2]		= "--enable-libv4l2,--without-libv4l2,libv4l2"
PACKAGECONFIG[mpg123]		= "--enable-mpg123,--disable-mpg123,mpg123"
PACKAGECONFIG[pulseaudio]	= "--enable-pulse,--disable-pulse,pulseaudio"
PACKAGECONFIG[soup]			= "--enable-soup,--disable-soup,libsoup-2.4"
PACKAGECONFIG[speex]		= "--enable-speex,--disable-speex,speex"
PACKAGECONFIG[taglib]		= "--enable-taglib,--disable-taglib,taglib"
PACKAGECONFIG[v4l2]			= "--enable-gst_v4l2,--disable-gst_v4l2"
PACKAGECONFIG[vpx]			= "--enable-vpx,--disable-vpx,libvpx"
PACKAGECONFIG[wavpack]		= "--enable-wavpack,--disable-wavpack,wavpack"
PACKAGECONFIG[x11]			= "--enable-x,--disable-x,${X11DEPENDS}"

# qt5 support is disabled, because it is not present in OE core, and requires more work than
# just adding a packageconfig (it requires access to moc, uic, rcc, and qmake paths).
# This is better done in a separate qt5 layer (which then should add a "qt5" packageconfig
# in a gstreamer1.0-plugins-bad bbappend).

EXTRA_OECONF += " \
	--enable-bz2 \
	--enable-oss \
	--enable-zlib \
	--disable-aalib \
	--disable-aalibtest \
	--disable-directsound \
	--disable-libcaca \
	--disable-libdv \
	--disable-oss4 \
	--disable-osx_audio \
	--disable-osx_video \
	--disable-qt \
	--disable-shout2 \
	--disable-waveform \
	"

FILES_${PN}-equalizer += "${datadir}/gstreamer-1.0/presets/*.prs"
