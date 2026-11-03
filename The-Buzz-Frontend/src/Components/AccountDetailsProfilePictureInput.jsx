import fileUploadIcon from "../assets/uploadIcon.svg";
import {useNavigate} from "react-router-dom";

function AccountDetailsProfilePictureInput({ id, imagePath = "http://localhost:8080/api/v1/user/profile-picture?time=0" }) {
    const navigate = useNavigate();

    const refreshImage = () => {
        document.getElementById(id).src = "http://localhost:8080/api/v1/user/profile-picture?time=" + Date.now();
        console.log( document.getElementById(id).src)
    }

    const uploadProfilePicture = () => {
        const file = document.getElementById("fileUploadInput").files[0]

        const formData = new FormData();
        formData.append("file", file);

        fetch("http://localhost:8080/api/v1/user/modify-profile-picture", {
            method: "POST",
            credentials: "include",
            body: formData
        }).then(res => {
            if (res.status === 201) {
                document.getElementById(id).src = "http://localhost:8080/api/v1/user/profile-picture";
            }
            if (res.status === 401) {
                console.log("unauthorized")
                navigate("/login");
            }
            if(res.status === 400) {
                console.log("Error modifying password | Bad Request");
                res.text().then(text => {
                    console.log("Error modifying password | Bad Request " + text);
                })
            }
        })
    }

    return (
        <div className="profile-picture">
            <label>Profile Picture</label>
            <img src={imagePath} id={id} alt="User's profile picture"/>
            <input id="fileUploadInput" type="file" onChange={uploadProfilePicture} />
            <div id="image-upload-identifier" onClick={() => {
                document.getElementById("fileUploadInput").click()
            }}>
                <img id="uploadFileIcon" src={fileUploadIcon} onClick={refreshImage} />
            </div>
        </div>
    )
}

export default AccountDetailsProfilePictureInput