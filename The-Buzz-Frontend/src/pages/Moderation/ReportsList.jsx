import Navbar from "../../Components/Navigation/Navbar.jsx";
import {useEffect, useState} from "react";
import "../css/reports-list.css"
import ReportListItem from "../../Components/ReportsPage/ReportListItem.jsx";
import LoadingCircle from "../../Components/LoadingCircle.jsx";
import ReportHandlingPopup from "../../Components/ReportsPage/ReportHandlingPopup.jsx";
import {useModeration} from "../../Contexts/ModerationContext.jsx";

function UserList() {
    const [ reportIdToHandle, setReportIdToHandle ] = useState(-1);
    const [ isHandling, setIsHandling ] = useState(false);
    const {reportData, reportLoadError, isLoadingReportData} = useModeration();

    const handleReport = (isHandlingReport, reportId) => {
        setReportIdToHandle(reportId)
        setIsHandling(isHandlingReport)
    }

    const closeHandleReport = () => {
        setReportIdToHandle(-1)
        setIsHandling(false)
    }

    const isEditingPageVisible = () => {
        if(!isLoadingReportData) {
            if (isHandling) {
                const reportIndex = reportData.findIndex((object) => object.reportId === reportIdToHandle);
                if(reportData.length > 0) {
                    return (
                        <div className="splitContentBox">
                            <div className="report-list">
                                {reportData.map((object, index) => (
                                    <ReportListItem key={object.reportId} contentType={object.contentType}
                                                    userWhoReported={object.reporterId}
                                                    reason={object.reason} dateReported={object.dateReported}
                                                    contentUUID={object.contentUUID}
                                                    contentId={object.contentId} reportId={object.reportId}
                                                    setIsHandling={handleReport}/>
                                ))}
                            </div>
                            <div className="reportHandlingPopup">
                            <ReportHandlingPopup key={reportIdToHandle} reportId={reportData[reportIndex].reportId}
                                                 contentType={reportData[reportIndex].contentType}
                                                 userWhoReported={reportData[reportIndex].reporterId}
                                                 reason={reportData[reportIndex].reason}
                                                 dateReported={reportData[reportIndex].dateReported}
                                                 contentUUID={reportData[reportIndex].contentUUID}
                                                 contentId={reportData[reportIndex].contentId}
                                                 closeHandlingReport={closeHandleReport}/>
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
            } else {
                if (reportData.length > 0) {
                    return (
                        <div className="splitContentBox">
                            <div className="report-list">
                                {reportData.map((object, index) => (
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
            if(reportLoadError.success !== null && reportLoadError.success === false ) {
                return <p>You are not authorized to access this resource</p>
            }
            return (<div className="centerdiv"><LoadingCircle /></div>)
        }
    }

    return (
        <>
            <Navbar />
            {isEditingPageVisible()}
        </>
    )
}

export default UserList;