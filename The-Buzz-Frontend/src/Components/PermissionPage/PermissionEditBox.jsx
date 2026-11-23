import { memo, useMemo } from "react";

const PermissionEditBox = memo(function PermissionEditBox({
                                                              keyName,
                                                              value,
                                                              allKeys,
                                                              allVals,
                                                              setValue
                                                          }) {

    const updatePermission = () => {
        const selectBox = document.getElementsByClassName("permissionSelectBox");
        let updateFullValArray = [];

        for(const currSelectBox of selectBox) {
            updateFullValArray.push(currSelectBox.value);
        }

        let indexOfKey = allKeys.indexOf(keyName);
        let currSelectBox = document.getElementById(keyName);

        updateFullValArray[indexOfKey] = currSelectBox.value;
        setValue(updateFullValArray);
        console.log(updateFullValArray);
    }

    const indexOfKey = allKeys.indexOf(keyName);

    const permissionOptions = useMemo(() => {
        // console.log("Regenerating options for:", keyName);

        if (indexOfKey === -1) return [];

        return allVals[indexOfKey].map((permission) => (
            <option
                key={`${keyName}-${permission}`}
                value={permission}
            >
                {permission}
            </option>
        ));
    }, [keyName, indexOfKey, allVals]);

    return (
        <div>
            <label htmlFor={keyName}>{keyName}</label>

            <select
                id={keyName}
                className="permissionSelectBox"
                value={value}
                onChange={updatePermission}
            >
                {permissionOptions}
            </select>
        </div>
    );
});

export default PermissionEditBox;