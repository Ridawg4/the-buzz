function PermissionEditBox( {keyName, value, allKeys, allVals, setValue}) {

    const updatePermission = () => {
        let indexOfKey = allKeys.indexOf(keyName);
        let updateFullValArray = allVals;

        updateFullValArray[indexOfKey] = 'allowed';
        setValue(updateFullValArray);
        console.log(allVals);
    }

    return (
        <div>
            <label>{keyName}</label>
            <button onClick={updatePermission}>{value}</button>
        </div>
    )
}

export default PermissionEditBox;