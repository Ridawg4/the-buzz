import Navbar from "../Components/Navbar.jsx";
import {useEffect, useState} from "react";
import "./css/reports-list.css"
import ReportListItem from "../Components/ReportListItem.jsx";
import LoadingCircle from "../Components/LoadingCircle.jsx";
import ReportHandlingPopup from "../Components/ReportHandlingPopup.jsx";

function UserList() {
    const [ reportEntries, setReportEntries ] = useState([]);
    const [ reportIdToHandle, setReportIdToHandle ] = useState(-1);
    const [ isHandling, setIsHandling ] = useState(false);
    const [ isLoading, setIsLoading ] = useState(true);

    const getReportEntries = () => {
        if (!reportEntries.length > 0) {
            fetch("http://localhost:8080/api/v1/reports", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                response.json().then((data) => {
                    let reports = data.reports

                    setReportEntries(reports)
                    setIsLoading(false);
                })
            })
        }
    }

    const handleReport = (isHandlingReport, reportId) => {
        setReportIdToHandle(reportId)
        setIsHandling(isHandlingReport)
    }

    const closeHandleReport = () => {
        setReportIdToHandle(-1)
        setIsHandling(false)
    }

    const isEditingPageVisible = () => {
        if(!isLoading) {
            if (isHandling) {
                const reportIndex = reportEntries.findIndex((object) => object.reportId === reportIdToHandle);
                if(reportEntries.length > 0) {
                    return (
                        <div className="splitContentBox">
                            <div className="report-list">
                                {reportEntries.map((object, index) => (
                                    <ReportListItem key={object.reportId} contentType={object.contentType}
                                                    userWhoReported={object.reporterId}
                                                    reason={object.reason} dateReported={object.dateReported}
                                                    contentUUID={object.contentUUID}
                                                    contentId={object.contentId} reportId={object.reportId}
                                                    setIsHandling={handleReport}/>
                                ))}
                            </div>
                            <ReportHandlingPopup key={reportIdToHandle} reportId={reportEntries[reportIndex].reportId}
                                                 contentType={reportEntries[reportIndex].contentType}
                                                 userWhoReported={reportEntries[reportIndex].reporterId}
                                                 reason={reportEntries[reportIndex].reason}
                                                 dateReported={reportEntries[reportIndex].dateReported}
                                                 contentUUID={reportEntries[reportIndex].contentUUID}
                                                 contentId={reportEntries[reportIndex].contentId}
                                                 closeHandlingReport={closeHandleReport}/>
                        </div>
                    )
                } else {
                    return (
                        <div className="splitContentBox">
                            <div className="report-list">
                                <p>No reports remain!</p>
                            </div>
                            <div className="reportHandlingPopup">
                            </div>
                        </div>
                    )
                }
            } else {
                getReportEntries();
                if (reportEntries.length > 0) {
                    return (
                        <div className="splitContentBox">
                            <div className="report-list">
                                {reportEntries.map((object, index) => (
                                    <ReportListItem key={object.reportId} contentType={object.contentType}
                                                    userWhoReported={object.reporterId}
                                                    reason={object.reason} dateReported={object.dateReported}
                                                    contentUUID={object.contentUUID}
                                                    contentId={object.contentId} reportId={object.reportId}
                                                    setIsHandling={handleReport}/>
                                ))}
                            </div>
                            <div className="reportHandlingPopup">
                                <p>Select a Report to get started!</p>
                            </div>
                        </div>
                    )
                } else {
                    return (
                        <div className="splitContentBox">
                            <div className="report-list">
                                <p>No reports remain!</p>
                            </div>
                            <div className="reportHandlingPopup">
                            </div>
                        </div>
                    )
                }
            }
        } else {
            return (<div className="centerdiv"><LoadingCircle /></div>)
        }
    }

    useEffect(() => {
        getReportEntries()
    })

    return (
        <>
            <Navbar />
            {isEditingPageVisible()}
        </>
    )
}

export default UserList;