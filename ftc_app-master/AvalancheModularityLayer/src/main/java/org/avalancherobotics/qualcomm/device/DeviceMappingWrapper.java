package org.avalancherobotics.qualcomm.device;

import org.avalancherobotics.standalone.device.DeviceDirectory;
import org.avalancherobotics.standalone.interfaces.IDevice;
import org.avalancherobotics.standalone.interfaces.IDeviceDirectory;

import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Map;

/** */
public class DeviceMappingWrapper<QC_DEVICE_TYPE extends HardwareDevice, SA_DEVICE_TYPE extends IDevice>
	extends DeviceDirectory<SA_DEVICE_TYPE>
{
	private /*@NonNull*/ DeviceWrapperFactory wrapperFactory;
	private /*@NonNull*/ HardwareMap.DeviceMapping<QC_DEVICE_TYPE> wrappedMapping;

	/** */
	public DeviceMappingWrapper(
		/*@NonNull*/ DeviceWrapperFactory wrapperFactory,
		/*@NonNull*/ HardwareMap.DeviceMapping<QC_DEVICE_TYPE> mappingToWrap )
	{
		this.wrapperFactory = wrapperFactory;
		this.wrappedMapping = mappingToWrap;
		if ( wrapperFactory == null )
			throw new IllegalArgumentException("The wrapperFactory parameter must be non-null.");
		if ( mappingToWrap == null )
			throw new IllegalArgumentException("The mappingToWrap parameter must be non-null.");

		this.sync();
	}

	private void sync()
	{
		clear();

		foreach(Map.Entry<String,QC_DEVICE_TYPE> entry : this.wrappedMapping.entrySet())
			put(entry.getKey(), wrapperFactory.createWrapper(entry.getValue()));
	}
}
